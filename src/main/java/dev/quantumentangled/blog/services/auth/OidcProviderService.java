package dev.quantumentangled.blog.services.auth;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public interface OidcProviderService {
    default String getUniqueId(OidcUser oidcUser) {
        return oidcUser.getSubject();
    };
    String getUsername(OidcUser oidcUser);
    String getEmail(OidcUser oidcUser);
    String getName(OidcUser oidcUser);
}