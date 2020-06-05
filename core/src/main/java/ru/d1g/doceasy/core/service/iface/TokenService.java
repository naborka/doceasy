package ru.d1g.doceasy.core.service.iface;


import ru.d1g.doceasy.postgres.model.StringToken;

import java.time.Duration;

public interface TokenService {
    StringToken findByToken(String token);
    StringToken create(Duration lifetime);
    StringToken create(Duration lifetime, String token);
    void dispose(StringToken stringToken);
    void remove(StringToken stringToken);
    boolean isValid(StringToken stringToken);
}
