package ru.d1g.doceasy.postgres.model;

import javax.persistence.Entity;
import java.time.Instant;

@Entity
public class StringToken extends BaseEntity {
    private String token;
    private Instant expiresAt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
