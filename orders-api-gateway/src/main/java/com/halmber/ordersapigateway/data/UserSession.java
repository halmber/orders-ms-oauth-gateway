package com.halmber.ordersapigateway.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@RedisHash("UserSession")
public class UserSession implements Serializable {
    @Id
    private String id;
    private String email;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant expiresAt;

    @JsonIgnore
    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}