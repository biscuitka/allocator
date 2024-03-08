package ru.biscuitka.allocator.quota;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.biscuitka.allocator.district.District;
import ru.biscuitka.allocator.district.DistrictRepository;
import ru.biscuitka.allocator.exception.NotFoundException;
import ru.biscuitka.allocator.resource.Resource;
import ru.biscuitka.allocator.resource.ResourceRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceQuotaService {
    private final ResourceQuotaRepository quotaRepository;
    private final ResourceRepository resourceRepository;
    private final DistrictRepository districtRepository;

    public List<ResourceQuotaDto> getAllQuotas(Pageable pageable) {
        List<ResourceQuota> resourceQuotas = quotaRepository.findAll(pageable).getContent();
        return ResourceQuotaMapper.fromQuotaToDtoList(resourceQuotas);
    }

    public ResourceQuotaDto getQuotaById(Long quotaId) {
        ResourceQuota quota = quotaRepository.findById(quotaId)
                .orElseThrow(() -> new NotFoundException("Quota with id=" + quotaId + " was not found"));
        return ResourceQuotaMapper.fromResourceQuotaToDto(quota);
    }

    public ResourceQuotaDto createQuota(NewResourceQuotaDto dto) {
        ResourceQuota quota = ResourceQuotaMapper.fromNewDtoToResourceQuota(dto);
        Resource resource = resourceRepository.findById(dto.getResource())
                .orElseThrow(() -> new NotFoundException("Resource with id=" + dto.getResource() + " was not found"));
        District district = districtRepository.findById(dto.getDistrict())
                .orElseThrow(() -> new NotFoundException("District with id=" + dto.getDistrict() + " was not found"));
        quota.setResource(resource);
        quota.setDistrict(district);
        ResourceQuota createdQuota = quotaRepository.save(quota);
        return ResourceQuotaMapper.fromResourceQuotaToDto(createdQuota);
    }

    public ResourceQuotaDto updateQuota(ResourceQuotaDto dto) {
        ResourceQuota savedQuota = quotaRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Quota with id=" + dto.getId() + " was not found"));

        Optional.ofNullable(dto.getQuantity()).ifPresent(savedQuota::setQuantity);
        Optional.ofNullable(dto.getStartDate()).ifPresent(savedQuota::setStartDate);
        Optional.ofNullable(dto.getEndDate()).ifPresent(savedQuota::setEndDate);

        if (dto.getResource() != null) {
            Resource resource = resourceRepository.findById(dto.getResource().getId())
                    .orElseThrow(() -> new NotFoundException("Resource with id=" + dto.getResource().getId() +
                            " was not found"));
            savedQuota.setResource(resource);
        }
        if (dto.getDistrict() != null) {
            District district = districtRepository.findById(dto.getDistrict().getId())
                    .orElseThrow(() -> new NotFoundException("District with id=" + dto.getDistrict().getId() +
                            " was not found"));
            savedQuota.setDistrict(district);
        }

        ResourceQuota updatedQuota = quotaRepository.save(savedQuota);
        return ResourceQuotaMapper.fromResourceQuotaToDto(updatedQuota);
    }

    public void deleteQuotaById(Long quotaId) {
        quotaRepository.findById(quotaId)
                .orElseThrow(() -> new NotFoundException("Quota with id=" + quotaId + " was not found"));
        quotaRepository.deleteById(quotaId);
    }
}
