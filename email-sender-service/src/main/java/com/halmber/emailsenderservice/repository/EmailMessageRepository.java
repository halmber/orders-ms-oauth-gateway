package com.halmber.emailsenderservice.repository;

import com.halmber.emailsenderservice.model.entity.EmailMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailMessageRepository extends ElasticsearchRepository<EmailMessage, String> {
    List<EmailMessage> findByStatus(EmailMessage.EmailStatus status);
}