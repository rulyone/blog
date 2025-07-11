package dev.quantumentangled.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    
    @Autowired
    private UmamiConfig.UmamiProperties umamiProperties;
    
    @ModelAttribute("umamiEnabled")
    public boolean umamiEnabled() {
        return umamiProperties.isEnabled();
    }
    
    @ModelAttribute("umamiScriptUrl")
    public String umamiScriptUrl() {
        return umamiProperties.getScriptUrl();
    }
    
    @ModelAttribute("umamiWebsiteId")
    public String umamiWebsiteId() {
        return umamiProperties.getWebsiteId();
    }
}