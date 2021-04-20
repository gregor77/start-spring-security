package com.rhyno.startsecurity.authentication;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityContextHolderTest {
    private static final String ANY_USER = "rhyno";
    private static final String ANY_PASSWORD = "password";
    private static final String USER_ROLE = "ROLE_USER";

    @BeforeEach
    void setUp() {
        // 멀티 쓰레드에서 ContextHolder에 인증된 사용자 정보 세팅시,
        // race condition 상태를 피하기 위해서 새로운 SecurityContext를 생성한다.
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);
        setMockAuthentication(ANY_USER, ANY_PASSWORD, USER_ROLE);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
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

    @Nested
    class MultiThread {
        class SampleThread extends Thread {
            @Override
            public void run() {
                super.run();

                Optional.ofNullable(SecurityContextHolder.getContext())
                        .map(SecurityContext::getAuthentication)
                        .orElseThrow(() -> new RuntimeException("Multi thread...authentication is null"));
            }
        }

        class SampleExceptionHandler implements Thread.UncaughtExceptionHandler {
            private String handlerName;
            private Runnable errorHandler;
            private CountDownLatch latch;

            public SampleExceptionHandler(String handlerName, Runnable errorHandler) {
                this.handlerName = handlerName;
                this.errorHandler = errorHandler;
            }

            public SampleExceptionHandler(String handlerName, Runnable errorHandler, CountDownLatch latch) {
                this.handlerName = handlerName;
                this.errorHandler = errorHandler;
                this.latch = latch;
            }

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(this.handlerName + " : " + e.getMessage());
                this.errorHandler.run();
                latch.countDown();
            }
        }

        @Test
        @DisplayName("try...catch 블록으로 thread 외부에서 exception을 핸들링 할 수 없다.")
        void errorCase() {
            Stream.of(new SampleThread(), new SampleThread())
                    .forEach(thread -> {
                        try {
                            thread.start();
                            assertThat(true).isTrue();
                        } catch (Exception e) {
                            Assertions.fail("try...catch 블록으로 thread exception 핸들링 할 수 없다.");
                        }
                    });
        }

        @Test
        @DisplayName("Multi Thread 에러 핸들링 - default thread exception handler 지정")
        void withDefaultExceptionHandler() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(2);
            Runnable defaultErrorHandler = mock(Runnable.class);
            Thread.setDefaultUncaughtExceptionHandler(new SampleExceptionHandler("defaultHandler", defaultErrorHandler, latch));

            Stream.of(new SampleThread(), new SampleThread()).forEach(SampleThread::start);
            latch.await();

            then(defaultErrorHandler).should(times(2)).run();
        }

        @Test
        @DisplayName("Multi Thread 에러 핸들링 - thread마다 exception handler 지정")
        void withExceptionHandlerEachThread() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(2);

            SampleThread firstThread = new SampleThread();
            Runnable firstExceptionHandler = mock(Runnable.class);
            firstThread.setUncaughtExceptionHandler(new SampleExceptionHandler("firstHandler", firstExceptionHandler, latch));

            SampleThread secondThread = new SampleThread();
            Runnable secondExceptionHandler = mock(Runnable.class);
            secondThread.setUncaughtExceptionHandler(new SampleExceptionHandler("secondHandler", secondExceptionHandler, latch));

            Stream.of(firstThread, secondThread).forEach(SampleThread::start);
            latch.await();

            then(firstExceptionHandler).should(times(1)).run();
            then(secondExceptionHandler).should(times(1)).run();
        }

        @Test
        @DisplayName("SecurityContextHolder에서 threadLocal mode변경을 통해서, multi thread에서 동기화 가능")
        void withGlobalMode() {
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
            setMockAuthentication(ANY_USER, ANY_PASSWORD, USER_ROLE);

            Runnable defaultErrorHandler = mock(Runnable.class);
            Thread.setDefaultUncaughtExceptionHandler(new SampleExceptionHandler("defaultHandler", defaultErrorHandler));

            Stream.of(new SampleThread(), new SampleThread()).forEach(SampleThread::start);

            then(defaultErrorHandler).should(never()).run();
        }
    }

    private void setMockAuthentication(String principal, String credential, String role) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication mockAuthentication = new TestingAuthenticationToken(principal, credential, role);
        context.setAuthentication(mockAuthentication);

        SecurityContextHolder.setContext(context);
    }
}
