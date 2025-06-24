package dev.quantumentangled.blog.services;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.repositories.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(request);

        String username = oauthUser.getAttribute("login");
        String email = oauthUser.getAttribute("email"); //could be null
        String name = oauthUser.getAttribute("name"); //could be null

        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setFullName(name);
            if(username.equals("rulyone")) {
                newUser.setRole("ROLE_AUTHOR");
            }else{
                newUser.setRole("ROLE_USER");
            }
            return userRepository.save(newUser);
        });

        // Create authority from role column
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return new DefaultOAuth2User(Collections.singleton(authority), oauthUser.getAttributes(), "login");
    }
}
