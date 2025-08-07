package dev.quantumentangled.blog.config;

import dev.quantumentangled.blog.filters.ProfileCompletionFilter;
import dev.quantumentangled.blog.services.UserService;
import dev.quantumentangled.blog.services.auth.CustomOAuth2UserService;
import dev.quantumentangled.blog.services.auth.CustomOidcUserService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;

    SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomOidcUserService customOidcUserService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public ProfileCompletionFilter profileCompletionFilter(UserService userService, 
                                                           RequestCache requestCache) {
        return new ProfileCompletionFilter(userService, requestCache);
    }

    @Bean
    public RequestCache requestCache() {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        return requestCache;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ProfileCompletionFilter profileCompletionFilter) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/static/**", "/login", "/logout", 
                    "/viewpost/**", "/portfolio", "/posts/**",
                    "/spatialindex", "/touchme-joystick/**",
                    "/uploads/**", "/css/**", "/img/**", "/js/**").permitAll()
                .requestMatchers("/writepost", "/posts/*/edit").hasRole("AUTHOR")
                .anyRequest().authenticated()                
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                .defaultSuccessUrl("/", false)
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                    .oidcUserService(customOidcUserService) 
                ))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .requestCache(cache -> cache
                .requestCache(requestCache()))
            .addFilterAfter(profileCompletionFilter, OAuth2LoginAuthenticationFilter.class);
        return http.build();
    }
}
