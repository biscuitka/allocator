package ru.biscuitka.allocator.district;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.biscuitka.allocator.constants.HeaderConstants;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/districts")
@RequiredArgsConstructor
public class DistrictController {
    private final DistrictService districtService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DistrictDto> getAll(@RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                    @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Запрос всех районов");
        return districtService.getAllDistricts(pageable);
    }

    @GetMapping("/{districtId}")
    @ResponseStatus(HttpStatus.OK)
    public DistrictDto getById(@PathVariable long districtId) {
        log.info("Запрос района по id: {}", districtId);
        return districtService.getDistrictById(districtId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DistrictDto create(@Valid @RequestBody DistrictDto districtDto) {
        log.info("Создание района: {}", districtDto);
        return districtService.createDistrict(districtDto);
    }

    @PatchMapping("/{districtId}")
    @ResponseStatus(HttpStatus.OK)
    public DistrictDto update(@PathVariable long districtId, @Valid @RequestBody DistrictDto districtDto) {
        districtDto.setId(districtId);
        log.info("Обновление/изменение района: {}", districtDto);
        return districtService.updateDistrict(districtDto);
    }

    @DeleteMapping("/{districtId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long districtId) {
        log.info("Удаление района по id: {}", districtId);
        districtService.deleteDistrictById(districtId);
    }
}
