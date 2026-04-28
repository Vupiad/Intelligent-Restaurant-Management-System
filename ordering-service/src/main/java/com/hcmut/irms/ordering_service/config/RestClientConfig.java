package com.hcmut.irms.ordering_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // 1. THE NORMAL BUILDER
    // @Primary tells Eureka: "Use this one by default so you don't crash!"
    @Bean
    @Primary
    public RestClient.Builder standardRestClientBuilder() {
        return RestClient.builder();
    }

    // 2. THE EUREKA-AWARE BUILDER
    // We give it a specific name so we can call it manually.
    @Bean(name = "loadBalancedBuilder")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    // 3. YOUR SPECIFIC MICROSERVICE CLIENTS
    // We use @Qualifier to specifically grab the Eureka-aware builder
    @Bean
    public RestClient menuServiceRestClient(@Qualifier("loadBalancedBuilder") RestClient.Builder builder) {
        return builder.baseUrl("http://menu-service").build();
    }
}
