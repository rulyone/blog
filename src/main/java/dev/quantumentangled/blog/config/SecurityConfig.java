package dev.quantumentangled.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/static/**", "/login", "/logout", 
                    "/viewpost/**", 
                    "/uploads/**", "/css/**", "/img/**", "/js/**").permitAll()
                .requestMatchers("/writepost", "/posts/*/edit").hasRole("AUTHOR")
                .anyRequest().authenticated()                
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/", false))
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .requestCache(cache -> cache
                .requestCache(requestCache));
        return http.build();
    }
}
