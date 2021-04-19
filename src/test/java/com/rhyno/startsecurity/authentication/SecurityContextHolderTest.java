package com.rhyno.startsecurity.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SecurityContextHolderTest {
    private static final String ANY_USER = "rhyno";
    private static final String ANY_PASSWORD = "password";
    private static final String USER_ROLE = "ROLE_USER";

    @BeforeEach
    void setUp() {
        // 멀티 쓰레드에서 ContextHolder에 인증된 사용자 정보 세팅시,
        // race condition 상태를 피하기 위해서 새로운 SecurityContext를 생성한다.
        setMockAuthentication(ANY_USER, ANY_PASSWORD, USER_ROLE);

    }

    @Test
    @DisplayName("SecurityContextHolder에 현재 인증된 사용자 정보를 세팅한다.")
    void securityContextHolder() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getName()).isEqualTo(ANY_USER);
        assertThat(authentication.getCredentials()).isEqualTo(ANY_PASSWORD);
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains(USER_ROLE);
    }

    @Test
    @DisplayName("Multi Thread에서 SecurityContextHolder sync가 맞지 않다. MODE_GLOBAL 전략을 사용하여 전략 변경이 필요하다")
    void basedOnMultiThread() {
        //TODO. thread extends해서 multi thread case만 구현
        //참고 : https://stackoverflow.com/questions/39515447/example-of-multithreading-of-java-8/39518519
    }

    private void setMockAuthentication(String principal, String credential, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication mockAuthentication = new TestingAuthenticationToken(principal, credential, role);
        context.setAuthentication(mockAuthentication);

        SecurityContextHolder.setContext(context);
    }
}
