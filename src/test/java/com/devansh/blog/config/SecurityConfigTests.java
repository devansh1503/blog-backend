package com.devansh.blog.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTests {
    @Test
    void createsFilterChainAndLoadsAdminFromSpringProperties() {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                    "ADMIN_USERNAME=test-admin", "ADMIN_PASSWORD=test-password");
            context.register(SecurityConfig.class);
            context.refresh();

            assertNotNull(context.getBean(SecurityFilterChain.class));
            var admin = context.getBean(UserDetailsService.class).loadUserByUsername("test-admin");
            assertTrue(context.getBean(PasswordEncoder.class)
                    .matches("test-password", admin.getPassword()));
            assertTrue(admin.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        }
    }
}
