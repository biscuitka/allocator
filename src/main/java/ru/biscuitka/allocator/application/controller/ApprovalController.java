package ru.biscuitka.allocator.application.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.biscuitka.allocator.application.dto.ApplicationDto;
import ru.biscuitka.allocator.application.service.ApprovalService;

import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/approval")
@RequiredArgsConstructor
public class ApprovalController {
    private final ApprovalService approvalService;

    @PostMapping("/start")
    public void startApproval() {
        log.info("Запуск проверки массовых заявок");
        approvalService.startApplicationApproval();
    }

    @PostMapping("/start/{quotaId}")
    public List<ApplicationDto> startApproval(@PathVariable Long quotaId) {
        log.info("Запуск проверки жеребьевочных заявок по квоте id:{}", quotaId);
        return approvalService.checkAndGetDistributedDrawApplications(quotaId);
    }

    @PostMapping("/stop")
    public void stopApproval() {
        log.info("Остановка проверки заявок");
        approvalService.stopApplicationApproval();
    }
}
