package dev.quantumentangled.blog.services.auth;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2ProviderService {
    String getUniqueId(OAuth2User oauthUser);
    String getUsername(OAuth2User oauthUser);
    String getEmail(OAuth2User oauthUser);
    String getName(OAuth2User oauthUser);
}