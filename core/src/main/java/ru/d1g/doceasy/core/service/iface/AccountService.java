package ru.d1g.doceasy.core.service.iface;


import ru.d1g.doceasy.postgres.model.Account;
import ru.d1g.doceasy.postgres.model.Identity;
import ru.d1g.doceasy.postgres.model.IdentityConfirmation;

import javax.annotation.Nullable;
import java.util.List;

public interface AccountService {
    Identity findIdentity(String identity);
    List<Identity> getIdentities(Account account);
    boolean isIdentityAlreadyExists(String identity);
    IdentityConfirmation emailConfirmation(String email, @Nullable Account account);
    Identity createIdentity(Identity.IdentityType identityType, String value);
    void confirmIdentity(IdentityConfirmation identityConfirmation, String confirmationToken);
    Account createAccount();
    Account save(Account account);
    Identity save(Identity account);
    boolean isPasswordValid(String password);
    boolean isPasswordMatches(Account account, String password);
    Account updatePassword(Account account, String rawPassword);
    boolean removeAccount(Account account, String password);
}
