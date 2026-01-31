package com.halmber.emailsenderservice.model.dto;

import com.halmber.emailsenderservice.model.entity.EmailMessage;
import lombok.Builder;

import java.time.Instant;

@Builder
public record EmailHistoryDto(
        String id,
        String recipientEmail,
        String subject,
        String content,
        EmailMessage.EmailStatus status,
        String errorMessage,
        Integer retryCount,
        Instant createdAt,
        Instant lastAttemptAt,
        Instant sentAt
) {
}
