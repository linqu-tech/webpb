package tech.linqu.webpb.sample.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.linqu.webpb.runtime.mvc.WepbRequestBodyAdvice;

/**
 * Configuration for beans.
 */
@Configuration
public class BeansConfiguration {

    /**
     * {@link WepbRequestBodyAdvice} bean.
     *
     * @return {@link WepbRequestBodyAdvice}
     */
    @Bean
    public WepbRequestBodyAdvice requestBodyAdvice() {
        return new WepbRequestBodyAdvice();
    }
}
