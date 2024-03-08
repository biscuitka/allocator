package ru.biscuitka.allocator.district;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import ru.biscuitka.allocator.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DistrictService {
    private final DistrictRepository districtRepository;

    @Transactional(readOnly = true)
    public List<DistrictDto> getAllDistricts(Pageable pageable) {
        List<District> districts = districtRepository.findAll(pageable).getContent();
        return DistrictMapper.fromDistrictToDtoList(districts);
    }


    @Transactional(readOnly = true)
    public DistrictDto getDistrictById(@PathVariable long districtId) {
        District savedDistrict = districtRepository.findById(districtId)
                .orElseThrow(() -> new NotFoundException("District with id=" + districtId + " was not found"));
        return DistrictMapper.fromDistrictToDto(savedDistrict);
    }


    public DistrictDto createDistrict(DistrictDto districtDto) {
        District district = DistrictMapper.fromDtoToDistrict(districtDto);
        District createdDistrict = districtRepository.save(district);
        return DistrictMapper.fromDistrictToDto(createdDistrict);
    }


    public DistrictDto updateDistrict(DistrictDto districtDto) {
        long districtId = districtDto.getId();
        District savedDistrict = districtRepository.findById(districtId)
                .orElseThrow(() -> new NotFoundException("District with id=" + districtId + " was not found"));

        if (districtDto.getName() != null) {
            savedDistrict.setName(districtDto.getName());
        }
        District updatedDistrict = districtRepository.save(savedDistrict);

        return DistrictMapper.fromDistrictToDto(updatedDistrict);
    }


    public void deleteDistrictById(long districtId) {
        districtRepository.findById(districtId)
                .orElseThrow(() -> new NotFoundException("District with id=" + districtId + " was not found"));
        districtRepository.deleteById(districtId);
    }
}

