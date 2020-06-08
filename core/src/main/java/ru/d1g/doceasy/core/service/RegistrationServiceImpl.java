package ru.d1g.doceasy.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.d1g.doceasy.core.data.repository.jpa.RegistrationRequestRepository;
import ru.d1g.doceasy.core.event.AccountRegistrationConfirmedEvent;
import ru.d1g.doceasy.core.event.AccountRegistrationInitiatedEvent;
import ru.d1g.doceasy.core.service.iface.AccountService;
import ru.d1g.doceasy.core.service.iface.RegistrationService;
import ru.d1g.doceasy.postgres.model.Account;
import ru.d1g.doceasy.postgres.model.Identity;
import ru.d1g.doceasy.postgres.model.RegistrationRequest;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final static Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Value("${doceasy.server.address}")
    private String serverAddress;

    private List<String> restrictedDomains;

    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final RegistrationRequestRepository registrationRequestRepository;
    private final AccountService accountService;
    private final MailSenderService mailSenderService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(AuthenticationManager authenticationManager, ApplicationEventPublisher eventPublisher, RegistrationRequestRepository registrationRequestRepository, AccountService accountService, MailSenderService mailSenderService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.eventPublisher = eventPublisher;
        this.registrationRequestRepository = registrationRequestRepository;
        this.accountService = accountService;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public RegistrationRequest request(RegistrationRequest request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setIdentityConfirmation(accountService.emailConfirmation(request.getEmail(), null));
        RegistrationRequest savedRequest = registrationRequestRepository.save(request);
        eventPublisher.publishEvent(new AccountRegistrationInitiatedEvent(this, savedRequest));
        return savedRequest;
    }

    @Override
    public RegistrationRequest getRequestById(UUID id) {
        return registrationRequestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional
    public Authentication confirm(RegistrationRequest registrationRequest, String confirmationToken) {
        Identity identity = registrationRequest.getIdentityConfirmation().getIdentity();
        if (identity.getAccount() != null) {
            throw new RuntimeException("already confirmed");
        }
        accountService.confirmIdentity(registrationRequest.getIdentityConfirmation(), confirmationToken);
        Account account = accountService.createAccount();
        account.setPassword(registrationRequest.getPassword());
        account.setActive(true);
        if (identity.getIdentityType() == Identity.IdentityType.EMAIL) {
            account.setEmail(identity.getValue());
        }
        identity.setAccount(account);
        accountService.save(account);
        accountService.save(identity);

        eventPublisher.publishEvent(new AccountRegistrationConfirmedEvent(this, account, registrationRequest));

        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(identity.getValue(), null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            return token;
        }
        return null;
    }

    @Override
    public void sendRegistrationEmail(RegistrationRequest registrationRequest) throws MessagingException {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverAddress + "/registration/confirm")
                .queryParam("registration", registrationRequest.getId())
                .queryParam("token", registrationRequest.getIdentityConfirmation().getStringToken().getToken());

        UriComponents uri = uriBuilder.build();

        HashMap<String, Object> registrationMailFields = new HashMap<>();
        registrationMailFields.put("name", registrationRequest.getPersonalName());
        registrationMailFields.put("confirmUrl", uri.toString());

        mailSenderService.send(registrationRequest.getEmail(), "Подтверждение регистрации учетной записи DocEASY", "mail/registration.ftl", registrationMailFields);
        log.debug("confirm registration url: " + uri.toString());
    }
}
