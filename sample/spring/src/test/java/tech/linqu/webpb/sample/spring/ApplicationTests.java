package tech.linqu.webpb.sample.spring;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(Application::new);
        try (MockedStatic<SpringApplication> application = mockStatic(SpringApplication.class)) {
            assertNotNull(application);
            Application.main(null);
        }
    }
}
