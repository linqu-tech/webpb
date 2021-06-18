package tech.linqu.webpb.sample.spring.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.linqu.webpb.runtime.mvc.WebpbHandlerMethodArgumentResolver;
import tech.linqu.webpb.runtime.mvc.WebpbRequestBodyAdvice;
import tech.linqu.webpb.runtime.reactive.WebpbClient;

/**
 * Configuration for beans.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * {@link WebpbRequestBodyAdvice} bean.
     *
     * @return {@link WebpbRequestBodyAdvice}
     */
    @Bean
    public WebpbRequestBodyAdvice requestBodyAdvice() {
        return new WebpbRequestBodyAdvice();
    }

    /**
     * {@link WebpbClient} bean.
     *
     * @param port server listening port
     * @return {@link WebpbClient}
     */
    @Bean
    public WebpbClient webpbClient(@Value("${server.port}") int port) {
        return new WebpbClient(WebClient.builder()
            .baseUrl("http://localhost:" + port)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new WebpbHandlerMethodArgumentResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }
}
