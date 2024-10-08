package org.dtristu.javaocr.documentsretriever.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestConfig {
    @Value(value = "${rest.user-uri}")
    String restClientUri;

    @Bean
    public RestClient restClientAccount(){
        return RestClient.create(restClientUri);
    }
}
