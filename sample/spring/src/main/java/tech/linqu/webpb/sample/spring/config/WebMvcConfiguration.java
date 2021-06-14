package tech.linqu.webpb.sample.spring.config;

import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.linqu.webpb.runtime.mvc.WepbRequestBodyAdvice;
import tech.linqu.webpb.runtime.reactive.WebpbClient;

/**
 * Configuration for beans.
 */
@EnableWebMvc
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * {@link WepbRequestBodyAdvice} bean.
     *
     * @return {@link WepbRequestBodyAdvice}
     */
    @Bean
    public WepbRequestBodyAdvice requestBodyAdvice() {
        return new WepbRequestBodyAdvice();
    }

    /**
     * {@link WebpbClient} bean.
     *
     * @param port server listening port
     * @return {@link WebpbClient}
     */
    @Bean
    public WebpbClient webpbClient(@Value("${server.port}") int port) throws MalformedURLException {
        return new WebpbClient(WebClient.builder().build(), new URL("http://127.0.0.1:" + port));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }
}
