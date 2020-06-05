package ru.d1g.doceasy.core.data.system;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.d1g.doceasy.postgres.model.Account;
import ru.d1g.doceasy.postgres.model.Identity;

import java.util.Collection;

public class DocEasyUserDetails extends User {
    private final Identity identity;

    public DocEasyUserDetails(Identity identity, Collection<? extends GrantedAuthority> authorities) {
        super(identity.getValue(), identity.getAccount().getPassword(), authorities);
        this.identity = identity;
    }

    public Account getAccount() {
        return identity.getAccount();
    }

    @Override
    public boolean isEnabled() {
        return identity.isConfirmed() && identity.getAccount().isActive();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }
}
