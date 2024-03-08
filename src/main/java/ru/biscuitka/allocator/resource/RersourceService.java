package ru.biscuitka.allocator.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.biscuitka.allocator.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RersourceService {
    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public List<ResourceDto> getAllResources(Pageable pageable) {
        List<Resource> resources = resourceRepository.findAll(pageable).getContent();
        return ResourceMapper.fromResourceToDtoList(resources);
    }

    @Transactional(readOnly = true)
    public ResourceDto getResourceById(long resourceId) {
        Resource savedResource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id=" + resourceId + " was not found"));
        return ResourceMapper.fromResourceToDto(savedResource);
    }

    public ResourceDto createResource(ResourceDto resourceDto) {
        Resource resource = ResourceMapper.fromDtoToResource(resourceDto);
        Resource createdResource = resourceRepository.save(resource);
        return ResourceMapper.fromResourceToDto(createdResource);
    }

    public ResourceDto updateResource(ResourceDto resourceDto) {
        long resourceId = resourceDto.getId();
        Resource savedResource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id=" + resourceId + " was not found"));
        if (resourceDto.getName() != null) {
            savedResource.setName(resourceDto.getName());
        }
        Resource updatedResource = resourceRepository.save(savedResource);
        return ResourceMapper.fromResourceToDto(updatedResource);
    }


    public void deleteResourceById(long resourceId) {
        resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Resource with id=" + resourceId + " was not found"));
        resourceRepository.deleteById(resourceId);

    }
}
