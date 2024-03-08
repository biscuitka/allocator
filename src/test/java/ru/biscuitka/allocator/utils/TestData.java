package ru.biscuitka.allocator.utils;

import ru.biscuitka.allocator.application.ApplicationStatus;
import ru.biscuitka.allocator.application.ApplicationType;
import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.application.dto.NewApplicationDto;
import ru.biscuitka.allocator.application.dto.UpdateApplicationDto;
import ru.biscuitka.allocator.district.DistrictDto;
import ru.biscuitka.allocator.quota.NewResourceQuotaDto;
import ru.biscuitka.allocator.quota.ResourceQuotaDto;
import ru.biscuitka.allocator.request.ResourceRequestDto;
import ru.biscuitka.allocator.resource.ResourceDto;
import ru.biscuitka.allocator.users.UserDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestData {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static List<String> districts = List.of("Вельский", "Котласский", "Онежский", "Пинежский", "Плесецкий");
    private static List<String> massResources = List.of("Утка", "Гусь", "Казарка");
    private static List<String> allResources = List.of("Утка", "Гусь", "Казарка", "Кабан", "Лось", "Волк");
    private static List<String> drawResources = List.of("Кабан", "Лось", "Волк");
    public static Long districtId = 100L;
    public static Long resourceId = 100L;
    public static Long quotaId = 100L;
    public static LocalDateTime start = LocalDateTime.now().withSecond(0).withNano(0).minusMonths(2);

    static Random random = new Random();
    public static LocalDate ticketDate = LocalDate.now().minusYears(1);


    public static DistrictDto districtDto1() {
        DistrictDto dto = new DistrictDto();
        dto.setId(1L);
        dto.setName(districts.get(0));
        return dto;
    }

    public static List<DistrictDto> createAllDistricts() {
        List<DistrictDto> districtsDtos = new ArrayList<>();
        for (int i = 1; i <= districts.size(); i++) {
            DistrictDto dto = new DistrictDto();
            dto.setId((long) i);
            dto.setName(districts.get(i - 1));
            districtsDtos.add(dto);
        }
        return districtsDtos;
    }

    public static ResourceDto resourceDto1() {
        ResourceDto dto = new ResourceDto();
        dto.setId(1L);
        dto.setName(massResources.get(0));
        return dto;
    }

    public static List<ResourceDto> createMassResources() {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        for (int i = 1; i <= massResources.size(); i++) {
            ResourceDto dto = new ResourceDto();
            dto.setId((long) i);
            dto.setName(massResources.get(i - 1));
            resourceDtos.add(dto);
        }
        return resourceDtos;
    }

    public static List<ResourceDto> createDrawResources() {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        for (int i = 1; i <= drawResources.size(); i++) {
            ResourceDto dto = new ResourceDto();
            dto.setId((long) i);
            dto.setName(drawResources.get(i - 1));
            resourceDtos.add(dto);
        }
        return resourceDtos;
    }

    public static NewResourceQuotaDto newQuotaDto1() {
        NewResourceQuotaDto dto = new NewResourceQuotaDto();
        dto.setResource(1L);
        dto.setDistrict(1L);
        dto.setQuantity(10L);
        dto.setStartDate(start);
        dto.setEndDate(start.plusMonths(2));
        return dto;
    }

    public static ResourceQuotaDto quotaDto1() {
        ResourceQuotaDto dto = new ResourceQuotaDto();
        dto.setId(1L);
        dto.setResource(resourceDto1());
        dto.setDistrict(districtDto1());
        dto.setQuantity(10L);
        dto.setStartDate(start);
        dto.setEndDate(start.plusMonths(2));
        return dto;
    }

    public static List<ResourceQuotaDto> createMassQuotas() {
        List<ResourceQuotaDto> quotaDtos = new ArrayList<>();
        List<ResourceDto> resourceDtos = createMassResources();
        List<DistrictDto> districtDtos = createAllDistricts();
        long i = 0;
        for (ResourceDto resource : resourceDtos) {
            for (DistrictDto district : districtDtos) {
                ResourceQuotaDto dto = new ResourceQuotaDto();
                dto.setId(++i);
                dto.setResource(resource);
                dto.setDistrict(district);
                dto.setQuantity(random.nextLong(5, 20));
                dto.setStartDate(start);
                dto.setEndDate(start.plusMonths(1));
                quotaDtos.add(dto);
            }
        }
        return quotaDtos;
    }

    public static UserDto userDto1() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Вася Пупкин");
        dto.setTicketSeries(1010L);
        dto.setTicketNumber(101010L);
        dto.setTicketDate(ticketDate);
        return dto;
    }

    public static List<ResourceRequestDto> createMassRequestDtos() {
        List<ResourceRequestDto> dtos = new ArrayList<>();
        List<ResourceDto> resources = createMassResources();
        DistrictDto district = districtDto1();
        for (int i = 1; i <= resources.size(); i++) {
            ResourceRequestDto requestDto = new ResourceRequestDto();
            requestDto.setId((long) i);
            requestDto.setResource(resources.get(i - 1).getId());
            requestDto.setDistrict(district.getId());
            requestDto.setQuantity(3L);
            dtos.add(requestDto);
        }
        return dtos;
    }

    public static NewApplicationDto newMassApplicationDto1() {
        NewApplicationDto dto = new NewApplicationDto();
        dto.setType(ApplicationType.MASS);
        return dto;
    }

    public static ApplicationDto applicationDto1() {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(1L);
        dto.setType(ApplicationType.MASS);
        dto.setStatus(ApplicationStatus.PENDING);
        dto.setCreated(start.plusWeeks(1));
        return dto;
    }

    public static UpdateApplicationDto updateApplicationDto1() {
        UpdateApplicationDto dto = new UpdateApplicationDto();
        dto.setId(1L);
        dto.setType(ApplicationType.MASS);
        return dto;
    }

    public static List<ApplicationDto> createApplicationDtoList() {
        List<ApplicationDto> applicationDtos = new ArrayList<>();
        UserDto applicant = TestData.userDto1();
        List<ResourceRequestDto> requestDtos = createMassRequestDtos();
        for (int i = 1; i <= 5; i++) {
            ApplicationDto dto = new ApplicationDto();
            dto.setId((long) i);
            dto.setType(ApplicationType.MASS);
            dto.setStatus(ApplicationStatus.PENDING);
            dto.setApplicant(applicant);
            requestDtos.forEach(r -> r.setApplication(dto.getId()));
            dto.setResourceRequests(requestDtos);
            dto.setCreated(start.plusWeeks(1));
            applicationDtos.add(dto);
        }
        return applicationDtos;
    }
}
