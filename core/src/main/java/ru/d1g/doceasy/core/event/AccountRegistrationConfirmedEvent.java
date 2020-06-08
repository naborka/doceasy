package ru.d1g.doceasy.core.event;

import org.springframework.context.ApplicationEvent;
import ru.d1g.doceasy.postgres.model.Account;
import ru.d1g.doceasy.postgres.model.RegistrationRequest;

public class AccountRegistrationConfirmedEvent extends ApplicationEvent {
    private final Account account;
    private final RegistrationRequest registrationRequest;

    public AccountRegistrationConfirmedEvent(Object source, Account account, RegistrationRequest registrationRequest) {
        super(source);
        this.account = account;
        this.registrationRequest = registrationRequest;
    }

    public Account getAccount() {
        return account;
    }

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }
}
