package ru.biscuitka.allocator.resource;

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
@RequestMapping(path = "/resources")
@RequiredArgsConstructor
public class ResourceController {
    private final RersourceService rersourceService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResourceDto> getAll(@RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                    @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Запрос всех ресурсов");
        return rersourceService.getAllResources(pageable);
    }

    @GetMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto getById(@PathVariable long resourceId) {
        log.info("Запрос ресурса по id: {}", resourceId);
        return rersourceService.getResourceById(resourceId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDto create(@Valid @RequestBody ResourceDto resourceDto) {
        log.info("Создание ресурса: {}", resourceDto);
        return rersourceService.createResource(resourceDto);
    }

    @PatchMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceDto update(@PathVariable long resourceId, @Valid @RequestBody ResourceDto resourceDto) {
        resourceDto.setId(resourceId);
        log.info("Обновление/изменение ресурса: {}", resourceDto);
        return rersourceService.updateResource(resourceDto);
    }

    @DeleteMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long resourceId) {
        log.info("Удаление ресурса по id: {}", resourceId);
        rersourceService.deleteResourceById(resourceId);
    }
}
