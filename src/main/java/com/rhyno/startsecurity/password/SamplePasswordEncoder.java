package com.rhyno.startsecurity.password;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SamplePasswordEncoder {
    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    public static PasswordEncoder delegatingPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public UserDetails getUserWithDefaultPasswordEncoder(String name, String password, String roles) {
        return User.withDefaultPasswordEncoder()
                .username(name)
                .password(password)
                .roles(roles)
                .build();
    }

    public UserDetails getUserWithEncodedPassword(String name, String password, PasswordEncodeType encodedType, String roles) {
        return User.builder()
                .username(name)
                .password(this.getEncodedPassword(encodedType, password))
                .roles(roles)
                .build();
    }

    public String getEncodedPassword(PasswordEncodeType type, String password) {
        PasswordEncoder passwordEncoder;

        if (PasswordEncodeType.BCRYPT.equals(type)) {
            passwordEncoder = new BCryptPasswordEncoder();
        } else {
            throw new IllegalArgumentException("not support encoded type." + type);
        }

        return "{" + type.getType() + "}" + passwordEncoder.encode(password);
    }
}
