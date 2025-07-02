package dev.quantumentangled.blog.services.auth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component("github")
public class OAuth2GitHubProviderService implements OAuth2ProviderService {
    
    @Override
    public String getUniqueId(OAuth2User oauthUser) {
        return oauthUser.getAttribute("id").toString();
    }
    
    @Override
    public String getUsername(OAuth2User oauthUser) {
        return oauthUser.getAttribute("login");
    }
    
    @Override
    public String getEmail(OAuth2User oauthUser) {
        return oauthUser.getAttribute("email");
    }
    
    @Override
    public String getName(OAuth2User oauthUser) {
        return oauthUser.getAttribute("name");
    }
}