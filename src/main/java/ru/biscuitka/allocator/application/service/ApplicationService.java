package ru.biscuitka.allocator.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.biscuitka.allocator.application.*;
import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.application.dto.NewApplicationDto;
import ru.biscuitka.allocator.application.dto.UpdateApplicationDto;
import ru.biscuitka.allocator.district.District;
import ru.biscuitka.allocator.district.DistrictRepository;
import ru.biscuitka.allocator.exception.NotFoundException;
import ru.biscuitka.allocator.request.*;
import ru.biscuitka.allocator.resource.Resource;
import ru.biscuitka.allocator.resource.ResourceRepository;
import ru.biscuitka.allocator.users.User;
import ru.biscuitka.allocator.users.UserDto;
import ru.biscuitka.allocator.users.UserMapper;
import ru.biscuitka.allocator.users.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ResourceRequestRepository requestRepository;
    private final ResourceRepository resourceRepository;
    private final DistrictRepository districtRepository;

    @Transactional(readOnly = true)
    public List<ApplicationDto> getAllApplications(List<ApplicationStatus> statuses, ApplicationType type,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusDays(7);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now();
        }
        List<Application> applications = applicationRepository
                .findAllByParams(statuses, type, rangeStart, rangeEnd, pageable);
        List<Long> applicationIds = applications.stream()
                .map(Application::getId)
                .toList();
        List<ResourceRequest> allRequests = requestRepository.findAllByApplicationIdIn(applicationIds);

        Map<Application, List<ResourceRequest>> applicationWithRequests = new HashMap<>();
        for (Application application : applications) {
            List<ResourceRequest> requests = new ArrayList<>();
            for (ResourceRequest request : allRequests) {
                if (request.getApplication().getId().equals(application.getId())) {
                    requests.add(request);
                }
            }
            applicationWithRequests.put(application, requests);
        }

        return ApplicationMapper.fromApplicationToDtoList(applicationWithRequests);
    }


    @Transactional(readOnly = true)
    public ApplicationDto getById(long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application with id=" + applicationId + " was not found"));
        List<ResourceRequest> requests = requestRepository.findAllByApplicationId(application.getId());
        return ApplicationMapper.fromApplicationToDto(application, requests);
    }


    public ApplicationDto createApplication(NewApplicationDto applicationDto) {
        Application application = ApplicationMapper.fromNewApplicationDtoToApplication(applicationDto);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreated(LocalDateTime.now());

        User applicant = UserMapper.fromDtoToUser(applicationDto.getApplicant());
        User requestedUser = userRepository.findUserByTicket(applicant.getTicketSeries(), applicant.getTicketNumber());
        if (requestedUser == null) {
            userRepository.save(applicant);
        }
        User savedApplicant = userRepository.findUserByTicket(applicant.getTicketSeries(), applicant.getTicketNumber());
        application.setApplicant(savedApplicant);

        Application savedApplication = applicationRepository.save(application);

        List<ResourceRequest> requests = new ArrayList<>();
        List<ResourceRequestDto> requestDtos = applicationDto.getResourceRequests();
        for (ResourceRequestDto dto : requestDtos) {
            Resource resource = resourceRepository.findById(dto.getResource())
                    .orElseThrow(() -> new NotFoundException("Resource with id=" + dto.getResource() + " was not found"));
            District district = districtRepository.findById(dto.getDistrict())
                    .orElseThrow(() -> new NotFoundException("District with id=" + dto.getDistrict() + " was not found"));
            ResourceRequest request = ResourceRequestMapper.fromDtoToResourceRequest(dto);
            request.setResource(resource);
            request.setDistrict(district);
            request.setStatus(ResourceStatus.PENDING);
            request.setApplication(application);
            requests.add(request);
        }


        List<ResourceRequest> savedRequests = requestRepository.saveAll(requests);
        return ApplicationMapper.fromApplicationToDto(savedApplication, savedRequests);
    }


    public ApplicationDto updateApplication(UpdateApplicationDto updateApplicationDto) {
        long applicationId = updateApplicationDto.getId();
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application with id=" + applicationId + " was not found"));

        Optional.ofNullable(updateApplicationDto.getType()).ifPresent(application::setType);

        if (updateApplicationDto.getApplicant() != null) {
            UserDto userForUpdate = updateApplicationDto.getApplicant();
            User savedUser = userRepository.findById(updateApplicationDto.getApplicant().getId())
                    .orElseThrow(() -> new NotFoundException("User with id=" +
                            updateApplicationDto.getApplicant().getId() + " was not found"));

            Optional.ofNullable(userForUpdate.getName()).ifPresent(savedUser::setName);
            Optional.ofNullable(userForUpdate.getTicketSeries()).ifPresent(savedUser::setTicketSeries);
            Optional.ofNullable(userForUpdate.getTicketNumber()).ifPresent(savedUser::setTicketNumber);
            Optional.ofNullable(userForUpdate.getTicketDate()).ifPresent(savedUser::setTicketDate);

            User updatedUser = userRepository.save(savedUser);
            application.setApplicant(updatedUser);
        }

        Application updatedApplication = applicationRepository.save(application);

        if (updateApplicationDto.getResourceRequests() != null) {
            List<ResourceRequestDto> resourceRequestsForUpdate = updateApplicationDto.getResourceRequests();
            List<ResourceRequest> savedRequests = requestRepository.findAllByApplicationId(applicationId);

            for (ResourceRequestDto dto : resourceRequestsForUpdate) {
                for (ResourceRequest request : savedRequests) {
                    if (dto.getResource() == request.getResource().getId() &&
                            dto.getDistrict() == request.getDistrict().getId()) {
                        Optional.ofNullable(dto.getQuantity()).ifPresent(request::setQuantity);
                        if (dto.getResource() != null) {
                            Resource resource = resourceRepository.findById(dto.getResource())
                                    .orElseThrow(() -> new NotFoundException("Resource with id=" +
                                            dto.getResource() + " was not found"));

                            request.setResource(resource);
                        }
                        if (dto.getDistrict() != null) {
                            District district = districtRepository.findById(dto.getDistrict())
                                    .orElseThrow(() -> new NotFoundException("District with id=" +
                                            dto.getDistrict() + " was not found"));
                            request.setDistrict(district);
                        }
                    }
                    requestRepository.save(request);
                }
            }
        }
        List<ResourceRequest> updatedRequests = requestRepository.findAllByApplicationId(applicationId);

        return ApplicationMapper.fromApplicationToDto(updatedApplication, updatedRequests);
    }
}
