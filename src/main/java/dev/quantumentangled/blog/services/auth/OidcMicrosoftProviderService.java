package dev.quantumentangled.blog.services.auth;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component("microsoft")
public class OidcMicrosoftProviderService implements OidcProviderService {
        
    @Override
    public String getUsername(OidcUser oidcUser) {
        return oidcUser.getAttribute("email");
    }
    
    @Override
    public String getEmail(OidcUser oidcUser) {
        return oidcUser.getAttribute("email");
    }
    
    @Override
    public String getName(OidcUser oidcUser) {
        return oidcUser.getAttribute("name");
    }
}