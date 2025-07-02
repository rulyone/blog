package dev.quantumentangled.blog.filters;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.savedrequest.RequestCache;

import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.services.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProfileCompletionFilter implements Filter {
    
    private final UserService userService;
    
    private final RequestCache requestCache;

    public ProfileCompletionFilter(UserService userService, RequestCache requestCache) {
        this.userService = userService;
        this.requestCache = requestCache;
    }
    
    private final Set<String> excludedPaths = Set.of(
        "/static/",
        "/login", 
        "/logout",
        "/complete-profile",
        "/uploads/",
        "/css/",
        "/img/",
        "/js/",
        "/error",
        "/favicon.ico"
    );
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        
        // Skip filter for excluded paths
        if (excludedPaths.stream().anyMatch(requestPath::startsWith)) {
            chain.doFilter(request, response);
            return;
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && 
            auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) auth;
            // Get the user_id that was added in CustomOAuth2UserService/CustomOidcUserService
            Long userId = (Long) token.getPrincipal().getAttribute("user_id");
            if (userId != null) {
                User user = userService.findById(userId);
                
                if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                    // Save the current request before redirecting
                    requestCache.saveRequest(httpRequest, httpResponse);
                    httpResponse.sendRedirect("/complete-profile");
                    return;
                }
            }
        }
        
        chain.doFilter(request, response);
    }
}
