package com.halmber.emailsenderservice.scheduler;

import com.halmber.emailsenderservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRetryScheduler {

    private final EmailService emailService;

    @Scheduled(fixedRateString = "${scheduler.fixed.rate}") // 5 minutes
    public void retryFailedEmails() {
        log.info("Scheduled retry task started");
        emailService.retryFailedMessages();
        log.info("Scheduled retry task completed");
    }
}