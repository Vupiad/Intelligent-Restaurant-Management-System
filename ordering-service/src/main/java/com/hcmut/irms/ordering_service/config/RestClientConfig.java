package com.hcmut.irms.ordering_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configures a {@link RestClient} pre-configured with the menu-service base URL.
 * Used by {@link com.hcmut.irms.ordering_service.adapter.external.MenuServiceAdapter}.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient menuServiceRestClient(
            @Value("${app.menu-service.base-url:http://localhost:8082}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
