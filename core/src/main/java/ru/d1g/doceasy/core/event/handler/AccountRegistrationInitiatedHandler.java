package ru.d1g.doceasy.core.event.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.d1g.doceasy.core.event.AccountRegistrationInitiatedEvent;
import ru.d1g.doceasy.core.service.iface.RegistrationService;

import javax.mail.MessagingException;

@Component
public class AccountRegistrationInitiatedHandler implements ApplicationListener<AccountRegistrationInitiatedEvent> {
    private final static Logger log = LoggerFactory.getLogger(AccountRegistrationInitiatedHandler.class);

    private final RegistrationService registrationService;

    public AccountRegistrationInitiatedHandler(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void onApplicationEvent(AccountRegistrationInitiatedEvent event) {
        try {
            registrationService.sendRegistrationEmail(event.getRegistrationRequest());
        } catch (MessagingException e) {
            log.error("Registration mail confirmation sending failed", e);
        }
    }
}
