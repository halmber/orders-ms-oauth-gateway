package com.halmber.springordersapi.service;

import com.halmber.springordersapi.model.dto.messaging.EmailMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageProducerService {
    private final KafkaOperations<String, EmailMessageDto> kafkaOperations;

    @Value("${kafka.topics.emailSend}")
    private String EMAIL_SEND_TOPIC;

    public void sendEmailNotification(String id, String recipientEmail, String subject, String content) {
        EmailMessageDto emailMessage = EmailMessageDto.builder()
                .id(id)
                .recipientEmail(recipientEmail)
                .subject(subject)
                .content(content)
                .build();

        kafkaOperations.send(EMAIL_SEND_TOPIC, id, emailMessage);
        log.info("Email notification sent to Kafka with id: {} for: {}", id, recipientEmail);
    }
}
