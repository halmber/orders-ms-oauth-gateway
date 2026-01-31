package com.halmber.emailsenderservice.service;

import com.halmber.emailsenderservice.model.dto.EmailHistoryDto;
import com.halmber.emailsenderservice.model.entity.EmailMessage;
import com.halmber.emailsenderservice.repository.EmailMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {

    private final EmailMessageRepository repository;

    public List<EmailHistoryDto> getSentEmails() {
        return repository.findByStatus(EmailMessage.EmailStatus.SENT)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<EmailHistoryDto> getFailedEmails() {
        return repository.findByStatus(EmailMessage.EmailStatus.FAILED)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private EmailHistoryDto toDto(EmailMessage entity) {
        return EmailHistoryDto.builder()
                .id(entity.getId())
                .recipientEmail(entity.getRecipientEmail())
                .subject(entity.getSubject())
                .content(entity.getContent())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .retryCount(entity.getRetryCount())
                .createdAt(entity.getCreatedAt())
                .lastAttemptAt(entity.getLastAttemptAt())
                .sentAt(entity.getSentAt())
                .build();
    }
}
