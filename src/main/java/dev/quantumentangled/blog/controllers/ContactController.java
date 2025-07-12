package dev.quantumentangled.blog.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import dev.quantumentangled.blog.controllers.forms.ContactForm;
import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.services.MailQueueService;
import dev.quantumentangled.blog.services.UserRateLimiter;
import dev.quantumentangled.blog.services.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactController {
    
    private final MailQueueService mailQueueService;
    private final UserService userService;
    private final UserRateLimiter userRateLimiter;

    @Value("${spring.mail.username}")
    private String configuredMail;

    public ContactController(MailQueueService mailQueueService, UserService userService, UserRateLimiter userRateLimiter) {
        this.mailQueueService = mailQueueService;
        this.userService = userService;
        this.userRateLimiter = userRateLimiter;
    }

    @GetMapping("/contact")
    public String contact(Model model,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        ContactForm form = new ContactForm();
        Long userId = oauth2User.getAttribute("user_id");
        User user = userService.findById(userId);
        form.setEmail(user.getIdentity().getProviderEmail());
        model.addAttribute("contactForm", form);
        return "contact";
    }

    @PostMapping("/contact")
    public String submit(@Valid @ModelAttribute ContactForm form,
            BindingResult result,
            @AuthenticationPrincipal OAuth2User oauth2User,
            Model model) {

        List<String> errors = new ArrayList<>();
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
            model.addAttribute("errors", errors);
            return "fragments/contact-validation :: contact-validation";
        }

        Long userId = oauth2User.getAttribute("user_id");
        if(!userRateLimiter.tryConsume(userId)){
            model.addAttribute("errors", List.of("You have reached the daily limit of 2 contact submissions. Please try again tomorrow."));
            return "fragments/contact-validation :: contact-validation";
        }
        
        mailQueueService.queueMail(configuredMail, "Contact Form submitted by " + form.getEmail() + " (" + userId + ")", form.getMessage());

        model.addAttribute("sent", true);
        return "fragments/contact-validation :: contact-success";
    }
    

}
