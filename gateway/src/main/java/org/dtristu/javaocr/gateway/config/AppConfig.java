package org.dtristu.javaocr.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value(value = "${user-service.uri}")
    private String userServiceUri;

    @Bean
    public WebClient localApiClient() {
        return WebClient.create(userServiceUri);
    }
}
