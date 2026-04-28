package com.hcmut.irms.ordering_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

/**
 * Security configuration — mirrors menu-service pattern exactly.
 * MANAGER and SERVER roles may create and read orders.
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Converter<Jwt, Collection<GrantedAuthority>> jwtRoleAuthoritiesConverter
    ) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName("sub");
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtRoleAuthoritiesConverter);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI — public
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Only MANAGER and SERVER can create / read orders
                        .requestMatchers(HttpMethod.POST, "/api/orders")
                                .hasAnyRole("MANAGER", "SERVER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**")
                                .hasAnyRole("MANAGER", "SERVER")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }

    @Bean
    Converter<Jwt, Collection<GrantedAuthority>> jwtRoleAuthoritiesConverter() {
        return new JwtRoleAuthoritiesConverter();
    }

    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }


    @Bean
    public JwtDecoder jwtDecoder(
            RestTemplate loadBalancedRestTemplate,
            @Value("${app.security.jwk-set-uri}") String jwkSetUri) {

        // We tell Nimbus (the underlying JWT library) to use our Eureka RestTemplate
        // instead of its default one.
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(loadBalancedRestTemplate)
                .build();
    }
}
