package com.halmber.emailsenderservice.controller;

import com.halmber.emailsenderservice.model.dto.EmailHistoryDto;
import com.halmber.emailsenderservice.service.EmailHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailHistoryService emailHistoryService;

    @GetMapping("/sent")
    public List<EmailHistoryDto> getSentEmails() {
        return emailHistoryService.getSentEmails();
    }

    @GetMapping("/failed")
    public List<EmailHistoryDto> getFailedEmails() {
        return emailHistoryService.getFailedEmails();
    }
}
