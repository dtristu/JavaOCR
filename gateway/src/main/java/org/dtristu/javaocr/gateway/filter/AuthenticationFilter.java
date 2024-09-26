package org.dtristu.javaocr.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }
    public static class Config {

    }
    @Autowired
    WebClient localApiClient;
    @Autowired
    private RouteValidator routeValidator;
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                return localApiClient.get()
                        .uri("/validate?token=" + authHeader)
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(response -> {
                            if (!"false".equals(response)) {
                                // If the token is valid, continue with the filter chain
                                ServerHttpRequest request = exchange.getRequest()
                                        .mutate()
                                        .header("name", response)
                                        .build();
                                ServerWebExchange exchangeMutated = exchange.mutate().request(request).build();
                                return chain.filter(exchangeMutated);
                            } else {
                                // If the token is invalid, throw an exception
                                return Mono.error(new RuntimeException("Unauthorized access to application"));
                            }
                        })
                        .onErrorResume(e -> {
                            // Handle any error, such as a network issue or invalid token
                            System.out.println("Invalid access: " + e.getMessage());
                            return Mono.error(new RuntimeException("Unauthorized access to application"));
                        });
            }
            return chain.filter(exchange);
        });
    }
}
