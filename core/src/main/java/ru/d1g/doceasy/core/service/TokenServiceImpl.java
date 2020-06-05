package ru.d1g.doceasy.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.repository.jpa.TokenRepository;
import ru.d1g.doceasy.core.service.iface.TokenService;
import ru.d1g.doceasy.postgres.model.QStringToken;
import ru.d1g.doceasy.postgres.model.StringToken;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private final static Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public StringToken findByToken(String token) {
        return tokenRepository.findOne(QStringToken.stringToken.token.eq(token)).orElseThrow(RuntimeException::new);
    }

    @Override
    public StringToken create(Duration lifetime) {
        return create(lifetime, UUID.randomUUID().toString());
    }

    @Override
    public StringToken create(Duration lifetime, String token) {
        StringToken stringToken = new StringToken();
        stringToken.setToken(token);
        stringToken.setExpiresAt(Instant.now().plus(lifetime));
        return tokenRepository.save(stringToken);
    }

    @Override
    public void dispose(StringToken stringToken) {
        stringToken.setExpiresAt(Instant.now());
        tokenRepository.save(stringToken);
    }

    @Override
    public void remove(StringToken stringToken) {
        tokenRepository.delete(stringToken);
    }

    @Override
    public boolean isValid(StringToken stringToken) {
        return stringToken.getExpiresAt().isAfter(Instant.now());
    }
}
