package ru.biscuitka.allocator.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.biscuitka.allocator.application.*;
import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.district.District;
import ru.biscuitka.allocator.exception.NotFoundException;
import ru.biscuitka.allocator.quota.ResourceQuota;
import ru.biscuitka.allocator.quota.ResourceQuotaRepository;
import ru.biscuitka.allocator.request.ResourceRequest;
import ru.biscuitka.allocator.request.ResourceRequestRepository;
import ru.biscuitka.allocator.request.ResourceStatus;
import ru.biscuitka.allocator.resource.Resource;
import ru.biscuitka.allocator.users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
   1. Заявки должны проверяться поочередно, в зависимости от срока поступления.

   2. Ресурсы в заявке одобряются, если уникальным заявителем запрошено количество ресурсов,
   не превышающих общий размер квоты на ресурс.

   3. Заявитель может получить одобрение разных ресурсов в разных заявках, при одобрении ресурса
   в рамках одного района, этот же ресурс в рамках других районах не одобряется.

   4. Ресурс отклоняется, если дата подачи заявки не вписывается в рамки сроков подачи на этот ресурс.

   Если у User1 есть approved заявка на Resource1 в District1, а обрабатываемая заявка от него же
   на Resource1 в District2, то отклонить.
   Если она тоже на Resource1 и District1, то пункт 2 - смотрим одобренное кол-во по всем approved заявкам
   и смотрим, впишемся ли в квоту если одобрим текущую заявку

 */

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ApprovalService {

    private final ApplicationRepository applicationRepository;
    private final ResourceQuotaRepository quotaRepository;
    private final ResourceRequestRepository requestRepository;
    private final ExecutorService checkThread = Executors.newSingleThreadExecutor();

    private volatile boolean isRunning = false;
    private volatile boolean isStopRequested = false;

    public void startApplicationApproval() {
        if (!isRunning) {
            isRunning = true;
            checkThread.submit(this::process);
        } else {
            log.info("Start auto check");
        }

    }

    public void stopApplicationApproval() {
        if (isRunning) {
            isStopRequested = true;
        } else {
            log.info("Can't stop check because it's not running");
        }

    }

    /**
     * Поток на каждой итерации запрашивает самую старую необработанную заявку и
     * в результате проверок присваивает ей статус
     */
    private void process() {
        try {
            while (!isStopRequested) {
                Application application = applicationRepository.findUnallocatedApplication(ApplicationStatus.PENDING, ApplicationType.MASS);
                if (application == null) {
                    break;
                }

                List<ResourceRequest> checkedRequests = checkMassApplication(application);
                if (checkedRequests.stream()
                        .allMatch(resourceRequest -> resourceRequest.getStatus().equals(ResourceStatus.DISTRIBUTED))) {
                    // если все запрашиваемые ресурсы распределены - заявка одобрена
                    application.setStatus(ApplicationStatus.APPROVED);
                } else if (checkedRequests.stream()
                        .allMatch(resourceRequest -> resourceRequest.getStatus().equals(ResourceStatus.REFUSED))) {
                    // если все запрашиваемые ресурсы отклонены - заявка отклонена
                    application.setStatus(ApplicationStatus.REJECTED);
                } else {
                    // если часть запрашиваемых ресурсов одобрена, а часть отклонена - заявка одобрена частично
                    application.setStatus(ApplicationStatus.PARTIALLY_APPROVED);
                }
                applicationRepository.save(application);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
            log.error("Cause: " + e.getCause());
            log.error("StackTrace: " + Arrays.toString(e.getStackTrace()));
        } finally {
            isRunning = false;
        }
    }

    public List<ApplicationDto> checkAndGetDistributedDrawApplications(Long quotaId) {
        List<ApplicationDto> dtos = new ArrayList<>();
        List<ResourceRequest> distributedRequests = checkDrawApplication(quotaId);
        List<Long> applicationIds = distributedRequests.stream()
                .map(resourceRequest -> resourceRequest.getApplication().getId())
                .toList();
        List<Application> distributedApplications = applicationRepository.findAllById(applicationIds);
        for (Application application : distributedApplications) {
            for (ResourceRequest request : distributedRequests) {
                if (request.getApplication().getId() == application.getId()) {
                    ApplicationDto dto = ApplicationMapper.fromApplicationToDto(application, List.of(request));
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }

    /**
     * Распределение ресурсов для заявок жеребьёвочного типа.
     * Предполагается, что в заявках такого типа только один запрос на ресурс одного типа
     * Предполагается, запускать этот метод вручную вне модуля автоматической проверки
     *
     * @param quotaId квота, по которой проводим жеребьевку (привязана к конкретному ресурсу и району)
     * @return проверенный список запросов на ресурс
     */
    private List<ResourceRequest> checkDrawApplication(Long quotaId) {

        ResourceQuota quota = quotaRepository.findById(quotaId)
                .orElseThrow(() -> new NotFoundException("Quota with id=" + quotaId + " was not found"));
        Resource resource = quota.getResource();
        District district = quota.getDistrict();

        //все необработанные жеребьевочные заявки по resource и district
        List<Application> drawApplications = applicationRepository.findAllByResourceAndDistrictAndTypeAndStatus(
                resource, district, ApplicationType.DRAW, ApplicationStatus.PENDING);

        List<Application> remainingApplications = new ArrayList<>(drawApplications);

        /*
        проверяем заявки по дате подачи и отсекаем поданные раньше или позже срока
        ? логично этот момент отслеживать на этапе подачи заявок
         */
        for (Application application : drawApplications) {
            if (checkApplicationDate(application.getCreated(), quota)) {
                remainingApplications.remove(application);
                application.setStatus(ApplicationStatus.REJECTED);
                log.info("Дата подачи заявки не соответствует сроку подачи, присвоен статус отказано: {}", application);
            }
        }

        long remainingQuota = quota.getQuantity();
        Random random = new Random();
        boolean isEnded = remainingQuota <= 0;

        /*
        запускаем цикл пока не будет исчерпан лимит квоты или не закончатся заявки
        выбираем рандомную заявку
        достаем запрос к этой заявке
        ищем все запросы данного пользователя на этот ресурс
        если нашлись еще запросы, то перебираем их и смотрим есть ли уже одобренный
        если находим одобренный, то этот запрос и заявку отклоняем
        */
        while (!isEnded && !remainingApplications.isEmpty()) {
            Application selectedApplication = remainingApplications.get(random.nextInt(remainingApplications.size()));
            ResourceRequest request = requestRepository.findByApplicationId(selectedApplication.getId());
            User applicant = selectedApplication.getApplicant();
            List<ResourceRequest> requests = requestRepository.findAllByApplicantIdAndResourceId(applicant.getId(), resource.getId());

            if (requests.size() > 1) {
                for (ResourceRequest r : requests) {
                    if (r.getId() != request.getId() && r.getStatus().equals(ResourceStatus.DISTRIBUTED)) {
                        request.setStatus(ResourceStatus.REFUSED);
                        selectedApplication.setStatus(ApplicationStatus.REJECTED);
                        log.info("Уже есть одобренная заявка на данный ресурс, " + " присвоен статус отказано: {}", selectedApplication);
                    }
                }
            }
            /*
            если прошла предыдущая проверка, смотрим лимит квоты и в зависимости от этого присваиваем статус
             */
            if (remainingQuota >= request.getQuantity()) {
                remainingQuota -= request.getQuantity();
                request.setStatus(ResourceStatus.DISTRIBUTED);
                selectedApplication.setStatus(ApplicationStatus.APPROVED);
                log.info("Заявка прошла проверки, присвоен статус одобрено: {}", selectedApplication);
            } else {
                request.setStatus(ResourceStatus.REFUSED);
                selectedApplication.setStatus(ApplicationStatus.REJECTED);
                log.info("Исчерпан лимит квоты, присвоен статус отказано: {}", selectedApplication);
            }

            remainingApplications.remove(selectedApplication);
            applicationRepository.save(selectedApplication);
            requestRepository.save(request);
        }

        if (!remainingApplications.isEmpty()) {
            List<Long> applicationIds = remainingApplications.stream()
                    .map(Application::getId)
                    .toList();
            List<ResourceRequest> remainingRequests = requestRepository.findAllByApplicationIdIn(applicationIds);
            for (ResourceRequest request : remainingRequests) {
                request.setStatus(ResourceStatus.REFUSED);
            }
            for (Application application : remainingApplications) {
                application.setStatus(ApplicationStatus.REJECTED);
            }
            requestRepository.saveAll(remainingRequests);
            applicationRepository.saveAll(remainingApplications);
            log.info("Исчерпан лимит квоты, оставшимся {} заявкам присвоен статус отказано", remainingApplications.size());
        }
        List<ResourceRequest> savedDistributedRequests = requestRepository
                .findAllByResourceIdAndStatus(resource.getId(), ResourceStatus.DISTRIBUTED);

        return savedDistributedRequests;
    }

    /**
     * Метод проверяет запросы конкретной заявки
     *
     * @param application самая старая необработанная заявка
     * @return список проверенных запросов
     */
    private List<ResourceRequest> checkMassApplication(Application application) {
        List<ResourceRequest> checkedRequests = new ArrayList<>();
        List<ResourceRequest> resourceRequests = requestRepository.findAllByApplicationId(application.getId());
        LocalDateTime createdDate = application.getCreated();
        User applicant = application.getApplicant();

        for (ResourceRequest request : resourceRequests) {
            Long resourceId = request.getResource().getId();
            ResourceQuota quota = quotaRepository.findByResourceIdAndDistrictId(request.getResource().getId(), request.getDistrict().getId());
            if (checkApplicationDate(createdDate, quota)) {
                request.setStatus(ResourceStatus.REFUSED);
                log.info("Дата подачи заявки не соответствует сроку подачи, присвоен статус отказано: {}", application);
                checkedRequests.add(request);
            } else {
                List<ResourceRequest> otherRequests = requestRepository.findAllByApplicantIdAndResourceId(applicant.getId(), resourceId);
                if (otherRequests.size() > 1) {
                    ResourceRequest checkedRequest = checkRequest(request, otherRequests, quota);
                    checkedRequests.add(checkedRequest);
                }
            }

        }
        return requestRepository.saveAll(checkedRequests);
    }


    private boolean checkApplicationDate(LocalDateTime created, ResourceQuota quota) {
        return created.isBefore(quota.getStartDate()) || created.isAfter(quota.getEndDate());
    }

    private boolean checkQuota(ResourceRequest request, ResourceQuota quota) {
        return request.getQuantity() <= quota.getQuantity();
    }

    /**
     * Проверяем чтобы данные запросы на ресурсы одобрялись в рамках одного района и не превышали квоту
     *
     * @param currentRequest текущий запрос для проверки
     * @param otherRequests  запросы на тот же ресурс из других заявок
     * @param quota          квоты для данных запросов
     * @return список проверенных запросов
     */
    private ResourceRequest checkRequest(ResourceRequest currentRequest, List<ResourceRequest> otherRequests,
                                         ResourceQuota quota) {

        for (ResourceRequest otherRequest : otherRequests) {
            if (currentRequest.getDistrict().getId() != otherRequest.getDistrict().getId()) {
                if (otherRequest.getStatus().equals(ResourceStatus.DISTRIBUTED)) {
                    currentRequest.setStatus(ResourceStatus.REFUSED);
                    log.info("Уже есть одобренная заявка на данный ресурс в другом районе, присвоен статус отказано: {}", currentRequest);
                } else {
                    if (checkQuota(currentRequest, quota)) {
                        currentRequest.setStatus(ResourceStatus.DISTRIBUTED);
                        log.info("В других районах заявок на данный ресурс нет, лимит " +
                                "квоты не превышен, присвоен статус одобрено: {}", currentRequest);
                    } else {
                        currentRequest.setStatus(ResourceStatus.REFUSED);
                        log.info("В других районах заявок на данный ресурс нет, но лимит " +
                                "квоты превышен, присвоен статус отказано: {}", currentRequest);
                    }
                }
            } else {
                long sumQuantity = currentRequest.getQuantity() + otherRequest.getQuantity();
                if (sumQuantity <= quota.getQuantity()) {
                    currentRequest.setStatus(ResourceStatus.DISTRIBUTED);
                    log.info("Уже есть одобренная заявка на данный ресурс в этом же районе, но лимит " +
                            "квоты не превышен, присвоен статус одобрено: {}", currentRequest);
                } else {
                    currentRequest.setStatus(ResourceStatus.REFUSED);
                    log.info("Уже есть одобренная заявка на данный ресурс в этом же районе, " +
                            "лимит квоты превышен, присвоен статус отказано: {}", currentRequest);
                }
            }
        }
        return currentRequest;
    }
}
