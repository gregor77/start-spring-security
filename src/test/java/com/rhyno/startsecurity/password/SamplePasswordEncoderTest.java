package com.rhyno.startsecurity.password;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SamplePasswordEncoderTest {
    private static final String ANY_USERNAME = "user";
    private static final String ANY_PASSWORD = "myPassword";
    private static final String USER_ROLE = "user";

    private SamplePasswordEncoder subject = new SamplePasswordEncoder();

    @Test
    @DisplayName("run bcrypt password encoder")
    void bcryptEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = SamplePasswordEncoder.bCryptPasswordEncoder();
        String result = bCryptPasswordEncoder.encode(ANY_PASSWORD);
        System.out.println(result);

        assertThat(bCryptPasswordEncoder.matches(ANY_PASSWORD, result)).isTrue();
    }

    @Nested
    class getUserWithDefaultPasswordEncoder {
        @Test
        @DisplayName("should return bcrypt encoded password with default BcryptPasswordEncoder")
        void defaultPasswordEncoder() {
            UserDetails user = subject.getUserWithDefaultPasswordEncoder(ANY_USERNAME, ANY_PASSWORD, USER_ROLE);

            assertThat(user.getPassword()).contains("{bcrypt}");

            // password encode를 돌리면 매번 다른 값이 출력된다.
            PasswordEncoder bcrpytPasswordEncoder = SamplePasswordEncoder.delegatingPasswordEncoder();

            assertThat(user.getPassword()).isNotEqualTo(bcrpytPasswordEncoder.encode(ANY_PASSWORD));
            assertThat(bcrpytPasswordEncoder.matches(ANY_PASSWORD, user.getPassword()))
                    .isTrue();
        }
    }

    @Nested
    class getUserWithEncodedPassword {
        @Test
        @DisplayName("should contain encoded pasasword and encoding type")
        void encodedPassword() {
            UserDetails user = subject.getUserWithEncodedPassword(ANY_USERNAME,
                    ANY_PASSWORD, PasswordEncodeType.BCRYPT, USER_ROLE);

            boolean isMatched = SamplePasswordEncoder.delegatingPasswordEncoder().matches(ANY_PASSWORD, user.getPassword());
            assertThat(isMatched).isTrue();
        }
    }

    @Nested
    class getEncodedPassword {
        @Test
        @DisplayName("throw IllegalArgumentException with unsupported encode type")
        void errorCase() {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> subject.getEncodedPassword(PasswordEncodeType.SHA256, ANY_PASSWORD));

            assertThat(error.getMessage()).isEqualTo("not support encoded type.SHA256");
        }

        @Test
        @DisplayName("should include encode type with encoded password")
        void normalCase() {
            String result = subject.getEncodedPassword(PasswordEncodeType.BCRYPT, ANY_PASSWORD);

            assertThat(result).startsWith("{bcrypt}");
            assertThat(SamplePasswordEncoder.delegatingPasswordEncoder().matches(ANY_PASSWORD, result));
        }

        @Test
        @DisplayName("throw error when encoded password not include type id")
        void sampleCase() {
            String passwordWithoutId = SamplePasswordEncoder.bCryptPasswordEncoder().encode(ANY_PASSWORD);

            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> SamplePasswordEncoder.delegatingPasswordEncoder().matches(ANY_PASSWORD, passwordWithoutId));

            assertThat(error.getMessage()).isEqualTo("There is no PasswordEncoder mapped for the id \"null\"");
        }
    }

    @Disabled
    @Nested
    class defaultBcryptPassword {
        @Test
        void showEncodedPassword() {
            // data.sql에 bcrpyt로 인코딩된 password를 입력하기 위해서 단위테스트로 수행
            String result = subject.getEncodedPassword(PasswordEncodeType.BCRYPT, "1111");
            System.out.println(result);

            result = subject.getEncodedPassword(PasswordEncodeType.BCRYPT, "2222");
            System.out.println(result);

            result = subject.getEncodedPassword(PasswordEncodeType.BCRYPT, "3333");
            System.out.println(result);
        }
    }
}