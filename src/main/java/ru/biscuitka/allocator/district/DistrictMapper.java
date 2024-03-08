package ru.biscuitka.allocator.district;

import java.util.ArrayList;
import java.util.List;

public class DistrictMapper {
    public static District fromDtoToDistrict(DistrictDto dto) {
        District district = new District();
        district.setName(dto.getName());
        return district;
    }

    public static DistrictDto fromDistrictToDto(District district) {
        DistrictDto dto = new DistrictDto();
        dto.setId(district.getId());
        dto.setName(district.getName());
        return dto;
    }

    public static List<DistrictDto> fromDistrictToDtoList(List<District> districts) {
        List<DistrictDto> districtDtos = new ArrayList<>();
        for (District district : districts) {
            districtDtos.add(fromDistrictToDto(district));
        }
        return districtDtos;
    }
}
