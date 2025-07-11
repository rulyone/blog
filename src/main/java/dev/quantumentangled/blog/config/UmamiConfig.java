package dev.quantumentangled.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// UmamiConfig.java
@Configuration
public class UmamiConfig {
    
    @Value("${umami.enabled:false}")
    private boolean enabled;
    
    @Value("${umami.script-url}")
    private String scriptUrl;
    
    @Value("${umami.website-id}")
    private String websiteId;
    
    @Bean
    public UmamiProperties umamiProperties() {
        return new UmamiProperties(enabled, scriptUrl, websiteId);
    }
    
    public static class UmamiProperties {
        private final boolean enabled;
        private final String scriptUrl;
        private final String websiteId;
        
        public UmamiProperties(boolean enabled, String scriptUrl, String websiteId) {
            this.enabled = enabled;
            this.scriptUrl = scriptUrl;
            this.websiteId = websiteId;
        }
        
        public boolean isEnabled() { return enabled; }
        public String getScriptUrl() { return scriptUrl; }
        public String getWebsiteId() { return websiteId; }
    }
}