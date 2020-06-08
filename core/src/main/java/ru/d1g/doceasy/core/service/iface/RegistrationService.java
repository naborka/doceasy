package ru.d1g.doceasy.core.service.iface;

import org.springframework.security.core.Authentication;
import ru.d1g.doceasy.postgres.model.RegistrationRequest;

import javax.mail.MessagingException;
import java.util.UUID;

public interface RegistrationService {
    RegistrationRequest request(RegistrationRequest request);
    RegistrationRequest getRequestById(UUID id);
    Authentication confirm(RegistrationRequest registrationRequest, String confirmationToken);
    void sendRegistrationEmail(RegistrationRequest registrationRequest) throws MessagingException;
}
