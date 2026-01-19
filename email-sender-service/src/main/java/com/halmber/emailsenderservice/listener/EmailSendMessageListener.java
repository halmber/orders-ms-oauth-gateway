package com.halmber.emailsenderservice.listener;

import com.halmber.emailsenderservice.model.dto.EmailMessageDto;
import com.halmber.emailsenderservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendMessageListener {
    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topics.emailSend}", groupId = "${spring.application.name}")
    public void onEmailSendMessage(EmailMessageDto emailDto) {
        log.info("Received message: {}", emailDto.subject());

        if (emailDto.id() == null || emailDto.id().isBlank()) {
            log.error("Received message without ID, skipping: {}", emailDto.subject());
            return;
        }
        emailService.processEmailMessage(emailDto);
    }
}
