package com.halmber.emailsenderservice.service;

import com.halmber.emailsenderservice.model.dto.EmailMessageDto;
import com.halmber.emailsenderservice.model.entity.EmailMessage;
import com.halmber.emailsenderservice.repository.EmailMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailMessageRepository repository;
    private final JavaMailSender mailSender;

    public void processEmailMessage(EmailMessageDto dto) {
        log.info("Processing email message with id: {} for: {}", dto.id(), dto.recipientEmail());

        // Check if message with this ID already exists
        Optional<EmailMessage> existingMessage = repository.findById(dto.id());

        if (existingMessage.isPresent()) {
            EmailMessage existing = existingMessage.get();
            log.info("Message with id {} already exists with status: {}", dto.id(), existing.getStatus());

            // If message was already sent successfully, skip processing
            if (existing.getStatus() == EmailMessage.EmailStatus.SENT) {
                log.info("Message with id {} already sent, skipping", dto.id());
                return;
            }

            // If message is pending or failed, update it and retry
            log.info("Updating existing message with id {} and retrying", dto.id());
            existing.setRecipientEmail(dto.recipientEmail());
            existing.setSubject(dto.subject());
            existing.setContent(dto.content());

            sendEmail(existing);
            return;
        }

        // Create new message
        EmailMessage emailMessage = EmailMessage.builder()
                .id(dto.id())
                .recipientEmail(dto.recipientEmail())
                .subject(dto.subject())
                .content(dto.content())
                .status(EmailMessage.EmailStatus.PENDING)
                .retryCount(0)
                .createdAt(Instant.now())
                .build();

        emailMessage = repository.save(emailMessage);
        log.info("Email message saved with id: {}", emailMessage.getId());

        sendEmail(emailMessage);
    }

    private void sendEmail(EmailMessage emailMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getRecipientEmail());
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getContent());

            mailSender.send(message);

            emailMessage.setStatus(EmailMessage.EmailStatus.SENT);
            emailMessage.setSentAt(Instant.now());
            emailMessage.setErrorMessage(null);

            repository.save(emailMessage);
            log.info("Email sent successfully to: {}", emailMessage.getRecipientEmail());

        } catch (MailException e) {
            log.error("Failed to send email to: {}. Error: {}",
                    emailMessage.getRecipientEmail(), e.getMessage());

            emailMessage.setStatus(EmailMessage.EmailStatus.FAILED);
            emailMessage.setErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
            emailMessage.setLastAttemptAt(Instant.now());
            emailMessage.setRetryCount(emailMessage.getRetryCount() + 1);

            repository.save(emailMessage);
        }
    }

    private List<EmailMessage> getFailedMessages() {
        return repository.findByStatus(EmailMessage.EmailStatus.FAILED);
    }

    public void retryFailedMessages() {
        log.info("Starting retry process for failed messages");
        List<EmailMessage> failedMessages = getFailedMessages();
        log.info("Found {} failed messages to retry", failedMessages.size());

        for (EmailMessage message : failedMessages) {
            log.info("Retrying email to: {} (attempt #{})",
                    message.getRecipientEmail(), message.getRetryCount() + 1);
            sendEmail(message);
        }

        log.info("Retry process completed");
    }
}