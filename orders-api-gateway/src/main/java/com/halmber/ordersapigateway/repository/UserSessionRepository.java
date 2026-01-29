package com.halmber.ordersapigateway.repository;

import com.halmber.ordersapigateway.auth.dto.UserInfo;
import com.halmber.ordersapigateway.data.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
public class UserSessionRepository {
    private final ReactiveRedisTemplate<String, UserSession> redisTemplate;

    public Mono<UserSession> createSession(UserInfo userInfo, Instant expiresAt) {
        UserSession userSession = new UserSession();
        userSession.setId(UUID.randomUUID().toString());
        userSession.setEmail(userInfo.getEmail());
        userSession.setName(userInfo.getName());
        userSession.setExpiresAt(expiresAt);

        Duration ttl = Duration.between(Instant.now(), expiresAt);
        return redisTemplate.opsForValue().set(userSession.getId(), userSession, ttl)
                .thenReturn(userSession);
    }

    public Mono<UserSession> findById(String id) {
        return redisTemplate.opsForValue().get(id);
    }
}
