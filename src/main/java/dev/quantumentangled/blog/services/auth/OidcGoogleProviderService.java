package dev.quantumentangled.blog.services.auth;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component("google")
public class OidcGoogleProviderService implements OidcProviderService {
        
    @Override
    public String getUsername(OidcUser oidcUser) {
        return oidcUser.getClaimAsString("email");
    }
    
    @Override
    public String getEmail(OidcUser oidcUser) {
        return oidcUser.getClaimAsString("email");
    }
    
    @Override
    public String getName(OidcUser oidcUser) {
        return oidcUser.getClaimAsString("name");
    }
}