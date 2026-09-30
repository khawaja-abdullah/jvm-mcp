package dev.jvmmcp.core.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class GlobMatcherTest {

    @ParameterizedTest(name = "Glob ''{1}'' matches ''{0}'' -> {2}")
    @CsvSource({
        "com.example.OrderService, *Service*, true",
        "orderService, *service*, true",
        "PaymentController, *Controller, true",
        "PaymentController, *Service, false",
        "userRepository, User*, true",
        "authManager, *auth*, true",
        "com.example.repo.UserRepo, com.example.*, true",
        "org.springframework.boot.Service, *spring*, true",
        "anything, *, true",
        "anything, '', true"
    })
    @DisplayName("GlobMatcher should match patterns case-insensitively")
    void shouldMatchGlobPatterns(String text, String pattern, boolean expected) {
        assertThat(GlobMatcher.matches(text, pattern)).isEqualTo(expected);
    }
}
