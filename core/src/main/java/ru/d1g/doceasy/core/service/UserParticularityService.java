package ru.d1g.doceasy.core.service;

import com.google.common.collect.Lists;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.d1g.doceasy.core.data.system.DocEasyUserDetails;
import ru.d1g.doceasy.core.service.iface.AccountService;
import ru.d1g.doceasy.postgres.model.Identity;

@Service
public class UserParticularityService implements UserDetailsService {

    private final AccountService accountService;

    public UserParticularityService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Identity identity = accountService.findIdentity(username);
        return new DocEasyUserDetails(identity, Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
