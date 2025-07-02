package dev.quantumentangled.blog.services.auth;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import dev.quantumentangled.blog.entities.Identity;
import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.repositories.IdentityRepository;
import dev.quantumentangled.blog.repositories.UserRepository;

import java.util.*;

@Service
public class CustomOidcUserService extends OidcUserService {
    
    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final Map<String, OidcProviderService> providerServices;
    
    public CustomOidcUserService(UserRepository userRepository,
                                IdentityRepository identityRepository,
                                Map<String, OidcProviderService> providerServices) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.providerServices = providerServices;
    }
    
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        
        OidcUser oidcUser = super.loadUser(userRequest);        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OidcProviderService providerService = providerServices.get(provider);
        if (providerService == null) {
            throw new OAuth2AuthenticationException("Unsupported OIDC provider: " + provider);
        }
        
        String providerId = providerService.getUniqueId(oidcUser);
        User user = findOrCreateUser(provider, providerId, providerService, oidcUser);
        
        // Create authority from role column
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        
        Map<String, Object> claims = new HashMap<>(oidcUser.getAttributes());
        claims.put("user_id", user.getId());
        OidcUserInfo info = new OidcUserInfo(claims);

        Collection<GrantedAuthority> updatedAuthorities = new ArrayList<>(oidcUser.getAuthorities());
        updatedAuthorities.add(authority);

        OidcUser customizedUser = new DefaultOidcUser(
            updatedAuthorities,
            oidcUser.getIdToken(),
            info
        );
        return customizedUser;
    }
    
    private User findOrCreateUser(String provider, String providerId, 
                                 OidcProviderService providerService, OidcUser oidcUser) {
                
        Optional<Identity> existingIdentity = identityRepository.findByProviderAndProviderId(provider, providerId);
        
        if (existingIdentity.isPresent()) {
            // Return existing user
            return userRepository.findById(existingIdentity.get().getUser().getId())
                .orElseThrow(() -> new OAuth2AuthenticationException("User not found"));
        }
        
        // Create new user
        User user = new User();
        user.setUsername(null); // Will be set during username setup flow
        user.setEmail(null);
        user.setFullName(null);
        user.setRole("ROLE_USER");
        user = userRepository.save(user);
        
        // Create identity
        Identity identity = new Identity();
        identity.setUser(user);
        identity.setProvider(provider);
        identity.setProviderId(providerId);
        identity.setProviderUsername(providerService.getUsername(oidcUser));
        identity.setProviderEmail(providerService.getEmail(oidcUser));
        identity.setProviderName(providerService.getName(oidcUser));
        identityRepository.save(identity);
        
        return user;
    }
    
}