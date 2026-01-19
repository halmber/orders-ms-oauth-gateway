package com.halmber.emailsenderservice.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.elasticsearch8.ElasticsearchLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "scheduling.enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M")
@RequiredArgsConstructor
public class SchedulerConfig {

    private final ElasticsearchClient elasticsearchClient;

    @Bean
    public LockProvider lockProvider() {
        return new ElasticsearchLockProvider(elasticsearchClient);
    }
}