package ru.biscuitka.allocator.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceMapper {
    public static Resource fromDtoToResource(ResourceDto dto) {
        Resource resource = new Resource();
        resource.setName(dto.getName());
        return resource;
    }

    public static ResourceDto fromResourceToDto(Resource resource) {
        ResourceDto dto = new ResourceDto();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        return dto;
    }

    public static List<ResourceDto> fromResourceToDtoList(List<Resource> resources) {
        List<ResourceDto> resourceDtos = new ArrayList<>();
        for (Resource resource : resources) {
            resourceDtos.add(fromResourceToDto(resource));
        }
        return resourceDtos;
    }
}
