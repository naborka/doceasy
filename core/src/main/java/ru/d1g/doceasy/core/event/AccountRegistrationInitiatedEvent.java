package ru.d1g.doceasy.core.event;


import org.springframework.context.ApplicationEvent;
import ru.d1g.doceasy.postgres.model.RegistrationRequest;

public class AccountRegistrationInitiatedEvent extends ApplicationEvent {
    private final RegistrationRequest registrationRequest;

    public AccountRegistrationInitiatedEvent(Object source, RegistrationRequest registrationRequest) {
        super(source);
        this.registrationRequest = registrationRequest;
    }

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }
}
