package dev.quantumentangled.blog.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import dev.quantumentangled.blog.controllers.forms.UserProfileForm;
import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.exceptions.UsernameAlreadyExistsException;
import dev.quantumentangled.blog.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class ProfileController {

    private final UserService userService;

    private final RequestCache requestCache;

    public ProfileController(UserService userService, @Lazy RequestCache requestCache) {
        this.userService = userService;
        this.requestCache = requestCache;
    }

    @GetMapping("/complete-profile")
    public String showProfileForm(Model model, Authentication authentication,
            HttpServletRequest request, HttpServletResponse response) {

        if (isProfileComplete(authentication)) {
            return redirectToOriginalUrl(request, response);
        }

        UserProfileForm form = new UserProfileForm();
        model.addAttribute("userForm", form);
        return "complete-profile";
    }

    @PostMapping("/complete-profile")
    public String completeProfile(@Valid @ModelAttribute UserProfileForm form,
            BindingResult result,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Model model) {

        List<String> errors = new ArrayList<>();
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
            model.addAttribute("errors", errors);
            return "fragments/username-validation :: username-validation";
        }

        try {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            Long userId = (Long) token.getPrincipal().getAttribute("user_id");

            userService.completeProfile(userId, form.getUsername());

            redirectAttributes.addFlashAttribute("message", "Profile completed successfully!");

            return redirectToOriginalUrl(request, response);

        } catch (UsernameAlreadyExistsException e) {
            errors.add("Username is already taken");
            model.addAttribute("errors", errors);
            return "fragments/username-validation :: username-validation";
        } catch (Exception e) {
            errors.add("An error ocurred while saving your profile");
            model.addAttribute("errors", errors);
            return "fragments/username-validation :: username-validation";
        }
    }

    private String redirectToOriginalUrl(HttpServletRequest request, HttpServletResponse response) {

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            UriComponents components = UriComponentsBuilder.fromUriString(targetUrl).build();
            String path = components.getPath();
            String query = components.getQuery();
            String relativeUrl = (path != null ? path : "/");
            if (query != null) {
                relativeUrl += "?" + query;
            }
            response.setHeader("HX-Redirect", relativeUrl);
            return null;
        }
        // fallback
        response.setHeader("HX-Redirect", "/");
        return null;
    }

    private boolean isProfileComplete(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            return false;
        }

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Long userId = (Long) token.getPrincipal().getAttribute("user_id");

        if (userId == null) {
            return false;
        }

        User user = userService.findById(userId);
        return user != null && user.getUsername() != null && !user.getUsername().trim().isEmpty();
    }
}