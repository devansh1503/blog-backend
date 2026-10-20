package com.devansh.blog.config;

import com.devansh.blog.controller.BookController;
import com.devansh.blog.controller.ChapterController;
import com.devansh.blog.service.BookService;
import com.devansh.blog.service.ChapterService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import jakarta.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CorsRegressionTests {
    @Test
    void appliesConfiguredOriginsBeforeAuthenticationAndPreservesWriteSecurity() throws Exception {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                    "ADMIN_USERNAME=test-admin", "ADMIN_PASSWORD=test-password",
                    "app.cors.allowed-origins=https://reader.example, https://admin.example, ,");
            context.register(SecurityConfig.class);
            context.refresh();

            MockMvc mvc = MockMvcBuilders.standaloneSetup(
                            new BookController(mock(BookService.class)),
                            new ChapterController(mock(ChapterService.class)))
                    .addFilters(context.getBean("springSecurityFilterChain", Filter.class))
                    .build();

            for (String path : new String[]{"/v1/book", "/v1/chapter"}) {
                for (String method : new String[]{"GET", "POST", "PUT", "PATCH", "DELETE"}) {
                    mvc.perform(options(path).header("Origin", "https://admin.example")
                                    .header("Access-Control-Request-Method", method)
                                    .header("Access-Control-Request-Headers", "authorization,content-type"))
                            .andExpect(status().isOk())
                            .andExpect(header().string("Access-Control-Allow-Origin", "https://admin.example"));
                }

                mvc.perform(post(path).header("Origin", "https://admin.example")
                                .contentType("application/json").content("{}"))
                        .andExpect(status().isUnauthorized())
                        .andExpect(header().string("Access-Control-Allow-Origin", "https://admin.example"));

                String credentials = Base64.getEncoder().encodeToString(
                        "test-admin:test-password".getBytes(StandardCharsets.UTF_8));
                mvc.perform(post(path).header("Origin", "https://admin.example")
                                .header("Authorization", "Basic " + credentials)
                                .contentType("application/json").content("{}"))
                        .andExpect(status().isOk())
                        .andExpect(header().string("Access-Control-Allow-Origin", "https://admin.example"));
            }

            mvc.perform(get("/v1/posts/getFeatured").header("Origin", "https://reader.example"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Access-Control-Allow-Origin", "https://reader.example"));
            mvc.perform(get("/v1/posts/getFeatured"))
                    .andExpect(status().isOk());

            for (String origin : new String[]{"https://untrusted.example", "http://localhost:5173"}) {
                mvc.perform(get("/v1/posts/getFeatured").header("Origin", origin))
                        .andExpect(status().isForbidden())
                        .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
                mvc.perform(options("/v1/book").header("Origin", origin)
                                .header("Access-Control-Request-Method", "POST"))
                        .andExpect(status().isForbidden());
            }

            mvc.perform(options("/v1/book").header("Origin", "https://admin.example")
                            .header("Access-Control-Request-Method", "POST")
                            .header("Access-Control-Request-Headers", "X-Unapproved"))
                    .andExpect(status().isForbidden());
            mvc.perform(options("/v1/book").header("Origin", "https://admin.example")
                            .header("Access-Control-Request-Method", "TRACE"))
                    .andExpect(status().isForbidden());
        }
    }
}
