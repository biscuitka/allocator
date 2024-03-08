package ru.biscuitka.allocator.request;

import java.util.ArrayList;
import java.util.List;

public class ResourceRequestMapper {
    public static ResourceRequest fromDtoToResourceRequest(ResourceRequestDto dto) {
        ResourceRequest request = new ResourceRequest();
        request.setQuantity(dto.getQuantity());
        request.setStatus(dto.getStatus());
        return request;
    }

    public static ResourceRequestDto fromResourceRequestToDto(ResourceRequest request) {
        ResourceRequestDto dto = new ResourceRequestDto();
        dto.setId(request.getId());
        dto.setStatus(request.getStatus());
        dto.setResource(request.getResource().getId());
        dto.setDistrict(request.getDistrict().getId());
        dto.setQuantity(request.getQuantity());
        dto.setApplication(request.getApplication().getId());
        return dto;
    }

    public static List<ResourceRequestDto> fromResourceRequestToDtoList(List<ResourceRequest> requests) {
        List<ResourceRequestDto> dtoList = new ArrayList<>();
        for (ResourceRequest request : requests) {
            dtoList.add(fromResourceRequestToDto(request));
        }
        return dtoList;
    }
}
