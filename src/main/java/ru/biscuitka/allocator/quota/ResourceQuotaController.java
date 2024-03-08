package ru.biscuitka.allocator.quota;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.biscuitka.allocator.constants.HeaderConstants;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/quotas")
@RequiredArgsConstructor
public class ResourceQuotaController {
    private final ResourceQuotaService quotaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<ResourceQuotaDto> getAll(@RequestParam(defaultValue = HeaderConstants.DEFAULT_FROM_VALUE) @Min(0) int from,
                                  @RequestParam(defaultValue = HeaderConstants.DEFAULT_SIZE_VALUE) int size) {
        log.info("Запрос всех квот");
        Pageable pageable = PageRequest.of(from / size, size);
        return quotaService.getAllQuotas(pageable);
    }

    @GetMapping("/{quotaId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceQuotaDto getById(@PathVariable long quotaId) {
        log.info("Запрос квоты по id: {}", quotaId);
        return quotaService.getQuotaById(quotaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceQuotaDto create(@Valid @RequestBody NewResourceQuotaDto quotaDto) {
        log.info("Создание квоты: {}", quotaDto);
        return quotaService.createQuota(quotaDto);
    }

    @PatchMapping("/{quotaId}")
    @ResponseStatus(HttpStatus.OK)
    public ResourceQuotaDto update(@PathVariable long quotaId, @Valid @RequestBody ResourceQuotaDto quotaDto) {
        quotaDto.setId(quotaId);
        log.info("Обновление/изменение квоты: {}", quotaDto);
        return quotaService.updateQuota(quotaDto);
    }

    @DeleteMapping("/{quotaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long quotaId) {
        log.info("Удаление квоты по id: {}", quotaId);
        quotaService.deleteQuotaById(quotaId);
    }
}
