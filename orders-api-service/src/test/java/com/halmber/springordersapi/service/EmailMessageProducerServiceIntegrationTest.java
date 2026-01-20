package com.halmber.springordersapi.service;

import com.halmber.springordersapi.AbstractKafkaIntegrationTest;
import com.halmber.springordersapi.KafkaTestHelper;
import com.halmber.springordersapi.model.dto.messaging.EmailMessageDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EmailMessageProducerService Integration Tests")
class EmailMessageProducerServiceIntegrationTest extends AbstractKafkaIntegrationTest {

    @Autowired
    private EmailMessageProducerService producerService;

    @Value("${kafka.topics.emailSend}")
    private String emailSendTopic;

    private KafkaMessageListenerContainer<String, EmailMessageDto> container;
    private BlockingQueue<ConsumerRecord<String, EmailMessageDto>> records;

    @BeforeEach
    void setUp() {
        records = KafkaTestHelper.createRecordsQueue();
        container = KafkaTestHelper.createEmailConsumer("test-customer-group", records);
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.stop();
        }
    }

    @Test
    @DisplayName("Should send email notification to Kafka topic")
    void shouldSendEmailNotificationToKafka() throws InterruptedException {
        // Given
        String id = "test-email-001";
        String recipientEmail = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // When
        producerService.sendEmailNotification(id, recipientEmail, subject, content);

        // Then
        ConsumerRecord<String, EmailMessageDto> record = records.poll(10, TimeUnit.SECONDS);

        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo(id);

        EmailMessageDto receivedDto = record.value();
        assertThat(receivedDto).isNotNull();
        assertThat(receivedDto.id()).isEqualTo(id);
        assertThat(receivedDto.recipientEmail()).isEqualTo(recipientEmail);
        assertThat(receivedDto.subject()).isEqualTo(subject);
        assertThat(receivedDto.content()).isEqualTo(content);
    }

    @Test
    @DisplayName("Should send multiple email notifications")
    void shouldSendMultipleEmailNotifications() throws InterruptedException {
        // Given
        String id1 = "test-email-001";
        String id2 = "test-email-002";
        String id3 = "test-email-003";

        // When
        producerService.sendEmailNotification(id1, "user1@example.com", "Subject 1", "Content 1");
        producerService.sendEmailNotification(id2, "user2@example.com", "Subject 2", "Content 2");
        producerService.sendEmailNotification(id3, "user3@example.com", "Subject 3", "Content 3");

        // Then
        ConsumerRecord<String, EmailMessageDto> record1 = records.poll(10, TimeUnit.SECONDS);
        ConsumerRecord<String, EmailMessageDto> record2 = records.poll(10, TimeUnit.SECONDS);
        ConsumerRecord<String, EmailMessageDto> record3 = records.poll(10, TimeUnit.SECONDS);

        assertThat(record1).isNotNull();
        assertThat(record2).isNotNull();
        assertThat(record3).isNotNull();

        assertThat(record1.value().id()).isEqualTo(id1);
        assertThat(record2.value().id()).isEqualTo(id2);
        assertThat(record3.value().id()).isEqualTo(id3);
    }

    @Test
    @DisplayName("Should handle special characters in email content")
    void shouldHandleSpecialCharactersInEmailContent() throws InterruptedException {
        // Given
        String id = "test-email-special";
        String recipientEmail = "test@example.com";
        String subject = "Special: «Тест» #123 & <tag>";
        String content = "Content with\nnewlines\tand\ttabs and 'quotes' and \"double quotes\"";

        // When
        producerService.sendEmailNotification(id, recipientEmail, subject, content);

        // Then
        ConsumerRecord<String, EmailMessageDto> record = records.poll(10, TimeUnit.SECONDS);

        assertThat(record).isNotNull();
        EmailMessageDto receivedDto = record.value();
        assertThat(receivedDto.subject()).isEqualTo(subject);
        assertThat(receivedDto.content()).isEqualTo(content);
    }

    @Test
    @DisplayName("Should use correct message key for partitioning")
    void shouldUseCorrectMessageKeyForPartitioning() throws InterruptedException {
        // Given
        String id = "test-email-key";
        String recipientEmail = "test@example.com";

        // When
        producerService.sendEmailNotification(id, recipientEmail, "Subject", "Content");

        // Then
        ConsumerRecord<String, EmailMessageDto> record = records.poll(10, TimeUnit.SECONDS);

        assertThat(record).isNotNull();
        assertThat(record.key()).isEqualTo(id);
    }
}

