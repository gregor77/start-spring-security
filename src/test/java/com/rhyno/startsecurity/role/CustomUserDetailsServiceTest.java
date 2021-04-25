package com.rhyno.startsecurity.role;

import com.rhyno.startsecurity.StartSecurityApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StartSecurityApplication.class)
class CustomUserDetailsServiceTest {
    private static final String USER1_EMAIL = "user1@gmail.com";
    private static final String USER2_EMAIL = "user2@gmail.com";
    private static final String ADMIN_EMAIL = "admin@gmail.com";

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Nested
    class loadUserByUsername {
        @Test
        @DisplayName("throw UsernameNotFoundException when user not found with email")
        void errorCase() {
            UsernameNotFoundException error = assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("not-found@gmail.com"));

            assertThat(error.getMessage()).isEqualTo("User is not found. email=not-found@gmail.com");
        }

        @Test
        @DisplayName("given user1 is temporary user, when get role, then has only communication authority")
        void checkAuthorityAsTemporaryUser() {
            UserDetails user1 = userDetailsService.loadUserByUsername(USER1_EMAIL);

            assertThat(user1.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .containsOnly("COMMUNICATION_AUTHORITY");
        }

        @Test
        @DisplayName("given user2 is user, when get role, then has communication, work, task authorities")
        void checkAuthorityAsUser() {
            UserDetails user2 = userDetailsService.loadUserByUsername(USER2_EMAIL);

            assertThat(user2.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("COMMUNICATION_AUTHORITY", "WORK_AUTHORITY", "TASK_AUTHORITY");
        }

        @Test
        @DisplayName("given admin is admin user, when get role, then has all of authorities")
        void checkAuthorityAsAdminUser() {
            UserDetails admin = userDetailsService.loadUserByUsername(ADMIN_EMAIL);

            assertThat(admin.getAuthorities())
                    .extracting(GrantedAuthority::getAuthority)
                    .contains("COMMUNICATION_AUTHORITY", "WORK_AUTHORITY", "TASK_AUTHORITY", "CONFIG_AUTHORITY");
        }
    }
}