package ru.biscuitka.allocator.application.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.biscuitka.allocator.application.ApplicationStatus;
import ru.biscuitka.allocator.application.ApplicationType;
import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.application.dto.NewApplicationDto;
import ru.biscuitka.allocator.application.dto.UpdateApplicationDto;
import ru.biscuitka.allocator.application.service.ApplicationService;
import ru.biscuitka.allocator.constants.HeaderConstants;
import ru.biscuitka.allocator.constants.UtilConstants;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ApplicationDto> getAll(@RequestParam(required = false) List<ApplicationStatus> statuses,
                                       @RequestParam(required = false) ApplicationType type,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = UtilConstants.DATETIME_FORMAT) LocalDateTime rangeStart,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = UtilConstants.DATETIME_FORMAT) LocalDateTime rangeEnd,
                                       @RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                       @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Запрос всех заявок");
        return applicationService.getAllApplications(statuses, type, rangeStart, rangeEnd, pageable);
    }

    @GetMapping("/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationDto getById(@PathVariable long applicationId) {
        log.info("Запрос заявки по id: {}", applicationId);
        return applicationService.getById(applicationId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationDto create(@Valid @RequestBody NewApplicationDto newApplicationDto) {
        log.info("Создание новой заявки: {}", newApplicationDto);
        return applicationService.createApplication(newApplicationDto);
    }

    @PatchMapping("/{applicationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApplicationDto updateApplication(@PathVariable long applicationId,
                                            @Valid @RequestBody UpdateApplicationDto updateApplicationDto) {
        updateApplicationDto.setId(applicationId);
        log.info("Обновление/изменение заявки: {}", updateApplicationDto);
        return applicationService.updateApplication(updateApplicationDto);
    }

}
