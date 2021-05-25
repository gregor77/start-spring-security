package com.rhyno.startsecurity.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private String email;
    private String credentials;

    public CustomAuthenticationToken(String email, String credentials) {
        super(Collections.emptyList());
        this.email = email;
        this.credentials = credentials;
    }

    public CustomAuthenticationToken(String email, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.email = email;
        this.credentials = credentials;
    }

    public CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }
}
