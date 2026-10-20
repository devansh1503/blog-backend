package com.devansh.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.ApplicationRunner;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.config.import=",
		"spring.mongodb.uri=mongodb://localhost:27017/blog_test",
		"ADMIN_USERNAME=test-admin",
		"ADMIN_PASSWORD=test-password",
		"app.cors.allowed-origins=http://localhost:5173"
})
class BlogApplicationTests {

	// Atlas index behavior is covered by MongoSearchIndexConfigTests.
	@MockitoBean(name = "initializeSearchIndex")
	private ApplicationRunner initializeSearchIndex;

	@Test
	void contextLoads() {
	}

}
