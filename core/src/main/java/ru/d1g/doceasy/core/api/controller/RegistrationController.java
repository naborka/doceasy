package ru.d1g.doceasy.core.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.d1g.doceasy.core.service.iface.AccountService;
import ru.d1g.doceasy.core.service.iface.RegistrationService;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private final static Logger log = LoggerFactory.getLogger(RegistrationController.class);

    private final AccountService accountService;
    private final RegistrationService registrationService;

    public RegistrationController(AccountService accountService, RegistrationService registrationService) {
        this.accountService = accountService;
        this.registrationService = registrationService;
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam UUID registration, @RequestParam String token, HttpServletResponse httpServletResponse) {
        Authentication confirmedRegistrationAuthentication = registrationService.confirm(registrationService.getRequestById(registration), token);
        if (confirmedRegistrationAuthentication != null) {
            SecurityContextHolder.getContext().setAuthentication(confirmedRegistrationAuthentication);
        }
        return ResponseEntity
                .status(302)
                .header("Location", "http://doceasy.local.test:8080/start/")
                .build();
    }
}
