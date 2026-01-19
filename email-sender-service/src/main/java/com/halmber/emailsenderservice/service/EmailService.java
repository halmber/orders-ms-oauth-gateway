package com.halmber.emailsenderservice.service;

import com.halmber.emailsenderservice.model.dto.EmailMessageDto;

public interface EmailService {
    void processEmailMessage(EmailMessageDto dto);
    void retryFailedMessages();
}
