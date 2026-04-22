package com.hcmut.irms.menu_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtRoleAuthoritiesConverterTests {
    private final JwtRoleAuthoritiesConverter converter = new JwtRoleAuthoritiesConverter();

    @Test
    void convertsSingleRoleToSpringAuthority() {
        Collection<GrantedAuthority> authorities = converter.convert(jwtWithRole("MANAGER"));
        assertThat(authorityNames(authorities)).containsExactly("ROLE_MANAGER");
    }

    @Test
    void keepsPrefixedRole() {
        Collection<GrantedAuthority> authorities = converter.convert(jwtWithRole("ROLE_SERVER"));
        assertThat(authorityNames(authorities)).containsExactly("ROLE_SERVER");
    }

    @Test
    void convertsRoleCollection() {
        Collection<GrantedAuthority> authorities = converter.convert(jwtWithRole(List.of("manager", "CHEF")));
        assertThat(authorityNames(authorities)).containsExactly("ROLE_MANAGER", "ROLE_CHEF");
    }

    @Test
    void returnsNoAuthoritiesWhenRoleClaimMissing() {
        Collection<GrantedAuthority> authorities = converter.convert(baseJwtBuilder().build());
        assertThat(authorityNames(authorities)).isEmpty();
    }

    private Jwt jwtWithRole(Object roleClaim) {
        return baseJwtBuilder()
                .claim("role", roleClaim)
                .build();
    }

    private Jwt.Builder baseJwtBuilder() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .claim("sub", "alice");
    }

    private List<String> authorityNames(Collection<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
