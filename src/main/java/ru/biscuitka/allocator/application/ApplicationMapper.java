package ru.biscuitka.allocator.application;

import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.application.dto.NewApplicationDto;
import ru.biscuitka.allocator.request.ResourceRequest;
import ru.biscuitka.allocator.request.ResourceRequestMapper;
import ru.biscuitka.allocator.users.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ApplicationMapper {
    public static Application fromNewApplicationDtoToApplication(NewApplicationDto dto) {
        Application application = new Application();
        application.setType(dto.getType());
        application.setApplicant(UserMapper.fromDtoToUser(dto.getApplicant()));
        return application;
    }

    public static ApplicationDto fromApplicationToDto(Application application, List<ResourceRequest> requests) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(application.getId());
        dto.setType(application.getType());
        dto.setCreated(application.getCreated());
        dto.setStatus(application.getStatus());
        dto.setApplicant(UserMapper.fromUserToDto(application.getApplicant()));
        dto.setResourceRequests(ResourceRequestMapper.fromResourceRequestToDtoList(requests));
        return dto;
    }

    public static List<ApplicationDto> fromApplicationToDtoList(Map<Application, List<ResourceRequest>> applicationsWithRequests) {
        List<ApplicationDto> dtoList = new ArrayList<>();
        for (Map.Entry<Application, List<ResourceRequest>> entry : applicationsWithRequests.entrySet()) {
            Application application = entry.getKey();
            List<ResourceRequest> requests = entry.getValue();
            ApplicationDto dto = fromApplicationToDto(application, requests);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
