package dev.quantumentangled.blog.services.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import dev.quantumentangled.blog.entities.Identity;
import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.repositories.IdentityRepository;
import dev.quantumentangled.blog.repositories.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final Map<String, OAuth2ProviderService> providerServices;

    public CustomOAuth2UserService(UserRepository userRepository, 
                                    IdentityRepository identityRepository,
                                    Map<String, OAuth2ProviderService> providerServices) {
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.providerServices = providerServices;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {

        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId();
        OAuth2ProviderService providerService = providerServices.get(provider);
        if (providerService == null) {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        String providerId = providerService.getUniqueId(oauthUser);

        User user = findOrCreateUser(provider, providerId, providerService, oauthUser);

        // Create authority from role column
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        attributes.put("user_id", user.getId());
        return new DefaultOAuth2User(Collections.singleton(authority), attributes, 
                                     getNameAttributeKey(provider));
    }

    private User findOrCreateUser(String provider, String providerId, OAuth2ProviderService providerService, OAuth2User oauthUser) {
        Optional<Identity> existingIdentity = identityRepository.findByProviderAndProviderId(provider, providerId);
        if (existingIdentity.isPresent()) {
            // Return existing user
            return userRepository.findById(existingIdentity.get().getUser().getId())
                    .orElseThrow(() -> new OAuth2AuthenticationException("User not found"));
        }

        User user = new User();
        user.setUsername(null); // Will be set during username setup flow
        user.setEmail(null);
        user.setFullName(null);
        user.setRole("ROLE_USER");
        user = userRepository.save(user);

        Identity identity = new Identity();
        identity.setUser(user);
        identity.setProvider(provider);
        identity.setProviderId(providerId);
        identity.setProviderUsername(providerService.getUsername(oauthUser));
        identity.setProviderEmail(providerService.getEmail(oauthUser));
        identity.setProviderName(providerService.getName(oauthUser));
        identityRepository.save(identity);

        return user;
    }
    
    private String getNameAttributeKey(String provider) {
        switch (provider) {
            case "github": return "login";
            default: throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }
    }
}
