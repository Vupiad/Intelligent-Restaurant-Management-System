package com.hcmut.irms.kds_service.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
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
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/ws/**",
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info"
                        ).permitAll()
                        .requestMatchers("/api/kds/**")
                        .hasAnyRole("MANAGER", "CHEF")
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
    public RestTemplate discoveryRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate plainRestTemplate() {
        return new RestTemplate();
    }


    @Bean
    public JwtDecoder jwtDecoder(
            @Qualifier("discoveryRestTemplate") RestTemplate discoveryRestTemplate,
            @Qualifier("plainRestTemplate") RestTemplate plainRestTemplate,
            @Value("${app.security.jwk-set-uri}") String jwkSetUri) {

        RestOperations restOperations = usesDirectHost(jwkSetUri)
                ? plainRestTemplate
                : discoveryRestTemplate;

        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .restOperations(restOperations)
                .build();
    }

    private boolean usesDirectHost(String jwkSetUri) {
        String host = URI.create(jwkSetUri).getHost();
        return host != null && Set.of("localhost", "127.0.0.1").contains(host);
    }
}
