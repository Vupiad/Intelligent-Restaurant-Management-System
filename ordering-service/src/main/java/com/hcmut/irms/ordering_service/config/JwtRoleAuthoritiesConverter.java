package com.hcmut.irms.ordering_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Converts the {@code "role"} JWT claim into Spring Security {@link GrantedAuthority} objects.
 * Supports both String (comma-separated) and Collection claim values.
 * Copied from menu-service to stay consistent across the microservices.
 */
public class JwtRoleAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLE_CLAIM  = "role";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return roleStream(jwt.getClaim(ROLE_CLAIM))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(this::toAuthorityName)
                .distinct()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
    }

    private Stream<String> roleStream(Object roleClaim) {
        if (roleClaim instanceof String role) {
            return Arrays.stream(role.split(","));
        }
        if (roleClaim instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString);
        }
        return Stream.empty();
    }

    private String toAuthorityName(String role) {
        String normalized = role.toUpperCase(Locale.ROOT);
        return normalized.startsWith(ROLE_PREFIX) ? normalized : ROLE_PREFIX + normalized;
    }
}
