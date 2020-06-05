package ru.d1g.doceasy.core.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.repository.jpa.AccountRepository;
import ru.d1g.doceasy.core.service.iface.AccountService;
import ru.d1g.doceasy.core.data.repository.jpa.IdentityConfirmationRepository;
import ru.d1g.doceasy.core.data.repository.jpa.IdentityRepository;
import ru.d1g.doceasy.core.service.iface.TokenService;
import ru.d1g.doceasy.postgres.model.*;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Value("${doceasy.identity.confirmation.token.lifetime}")
    private Duration identityConfirmationTokenDuration;
    @Value("${doceasy.account.password.minimum-length}")
    private int MINIMAL_PASSWORD_LENGTH;

    private final AccountRepository accountRepository;
    private final IdentityRepository identityRepository;
    private final IdentityConfirmationRepository identityConfirmationRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, IdentityRepository identityRepository, IdentityConfirmationRepository identityConfirmationRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.identityRepository = identityRepository;
        this.identityConfirmationRepository = identityConfirmationRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Identity findIdentity(String identity) {
        return identityRepository.findOne(QIdentity.identity.value.eq(identity)).orElseThrow(RuntimeException::new);
    }

    @Override
    public List<Identity> getIdentities(Account account) {
        return Lists.newArrayList(identityRepository.findAll(QIdentity.identity.account.eq(account).and(QIdentity.identity.confirmed.isTrue())));
    }

    @Override
    public boolean isIdentityAlreadyExists(String identity) {
        return identityRepository.exists(QIdentity.identity.value.eq(identity));
    }

    @Override
    @Transactional
    public IdentityConfirmation emailConfirmation(String email, @Nullable Account account) {
        Identity identity = createIdentity(Identity.IdentityType.EMAIL, email);
        identity.setAccount(account);
        IdentityConfirmation confirmation = new IdentityConfirmation();
        confirmation.setIdentity(identity);
        confirmation.setStringToken(tokenService.create(identityConfirmationTokenDuration));
        return identityConfirmationRepository.save(confirmation);
    }

    @Override
    public Identity createIdentity(Identity.IdentityType identityType, String value) {
        if (isIdentityAlreadyExists(value)) {
            if (findIdentity(value).isConfirmed()) {
                throw new RuntimeException("identity already in use");
            }
            return findIdentity(value);
        }
        Identity identity = new Identity();
        identity.setConfirmed(false);
        identity.setIdentityType(identityType);
        identity.setValue(value);
        return save(identity);
    }

    @Override
    public void confirmIdentity(IdentityConfirmation identityConfirmation, String confirmationToken) {
        StringToken validationToken = identityConfirmation.getStringToken();
        if (identityConfirmation.getStringToken().getToken().equals(confirmationToken)) {
            if (tokenService.isValid(validationToken)) {
                Identity identity = identityConfirmation.getIdentity();
                identity.setConfirmed(true);
                tokenService.dispose(validationToken);
            } else {
                throw new RuntimeException("confirmation token expired");
            }
        } else {
            throw new RuntimeException("invalid confirmation token");
        }
    }

    @Override
    public Identity save(Identity identity) {
        return identityRepository.save(identity);
    }

    @Override
    public Account createAccount() {
        Account account = new Account();
        account.setActive(false);
        return account;
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public boolean isPasswordValid(String password) {
        return password.length() >= MINIMAL_PASSWORD_LENGTH;
    }

    @Override
    public boolean isPasswordMatches(Account account, String password) {
        return passwordEncoder.matches(password, account.getPassword());
    }

    @Override
    public Account updatePassword(Account account, String rawPassword) {
        if (isPasswordValid(rawPassword)) {
            String newPasswordEncoded = passwordEncoder.encode(rawPassword);
            account.setPassword(newPasswordEncoded);
            return save(account);
        }
        throw new RuntimeException("Password invalid");
    }

    @Override
    public boolean removeAccount(Account account, String password) {
        if (passwordEncoder.matches(password, account.getPassword())) {
            account.setActive(false);
            save(account);
            SecurityContextHolder.clearContext();
            return true;
        }
        return false;
    }
}
