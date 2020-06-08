package ru.d1g.doceasy.core.event.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.d1g.doceasy.core.event.AccountRegistrationConfirmedEvent;

@Component
public class AccountRegistrationConfirmedHandler implements ApplicationListener<AccountRegistrationConfirmedEvent> {
    private final static Logger log = LoggerFactory.getLogger(AccountRegistrationConfirmedHandler.class);

    public AccountRegistrationConfirmedHandler() {
    }

    @Override
    public void onApplicationEvent(AccountRegistrationConfirmedEvent event) {
        log.debug("registration confirmed for account {}", event.getAccount().getEmail());
    }
}
