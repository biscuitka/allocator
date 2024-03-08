package ru.biscuitka.allocator.quota;

import ru.biscuitka.allocator.district.DistrictMapper;
import ru.biscuitka.allocator.resource.ResourceMapper;

import java.util.ArrayList;
import java.util.List;

public class ResourceQuotaMapper {

    public static ResourceQuota fromNewDtoToResourceQuota(NewResourceQuotaDto dto) {
        ResourceQuota quota = new ResourceQuota();
        quota.setQuantity(dto.getQuantity());
        quota.setStartDate(dto.getStartDate());
        quota.setEndDate(dto.getEndDate());
        return quota;
    }

    public static ResourceQuotaDto fromResourceQuotaToDto(ResourceQuota quota) {
        ResourceQuotaDto dto = new ResourceQuotaDto();
        dto.setId(quota.getId());
        dto.setResource(ResourceMapper.fromResourceToDto(quota.getResource()));
        dto.setDistrict(DistrictMapper.fromDistrictToDto(quota.getDistrict()));
        dto.setQuantity(quota.getQuantity());
        dto.setStartDate(quota.getStartDate());
        dto.setEndDate(quota.getEndDate());
        return dto;
    }

    public static List<ResourceQuotaDto> fromQuotaToDtoList(List<ResourceQuota> resourceQuotas) {
        List<ResourceQuotaDto> dtos = new ArrayList<>();
        for (ResourceQuota quota : resourceQuotas) {
            dtos.add(fromResourceQuotaToDto(quota));
        }
        return dtos;
    }

}
