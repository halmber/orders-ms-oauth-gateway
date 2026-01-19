package com.halmber.emailsenderservice.model.dto;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EmailMessageDto(
        String id,
        String recipientEmail,
        String subject,
        String content
) {
}