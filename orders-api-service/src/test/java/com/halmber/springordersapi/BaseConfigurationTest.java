package com.halmber.springordersapi;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "spring.kafka.admin.enabled=false",
        "spring.kafka.bootstrap-servers=disabled:9092",
        "spring.cloud.consul.enabled=false",
        "spring.cloud.consul.config.enabled=false",
        "spring.cloud.consul.discovery.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@ActiveProfiles("test")
public abstract class BaseConfigurationTest {
}