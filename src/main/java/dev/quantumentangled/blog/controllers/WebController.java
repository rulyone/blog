package dev.quantumentangled.blog.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import dev.quantumentangled.blog.entities.BlogPost;
import dev.quantumentangled.blog.repositories.BlogPostRepository;
import dev.quantumentangled.blog.services.MarkdownService;


@Controller
public class WebController {

    private final BlogPostRepository postRepository;
    private final MarkdownService markdownService;

    public WebController(BlogPostRepository postRepository, MarkdownService markdownService) {
        this.postRepository = postRepository;
        this.markdownService = markdownService;
    }

    @GetMapping("/")
    public String blogPage(Model model) {
        List<BlogPost> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Convert Markdown to HTML for each post
        List<Map<String, Object>> renderedPosts = posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", post.getTitle());
            map.put("htmlContent", markdownService.renderToHtml(post.getContent()));
            map.put("createdAt", post.getCreatedAt());
            return map;
        }).toList();

        model.addAttribute("posts", renderedPosts);
        return "blog";
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/posts/{id}/edit")
    public String editPost(@PathVariable Long id, Model model) {
        BlogPost post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        model.addAttribute("post", post);
        return "editpost";
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @PutMapping("/posts/{id}")
    @ResponseBody
    public String updatePost(@PathVariable Long id,
                            @RequestParam String title,
                            @RequestParam String content) {

        BlogPost post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        post.setTitle(title);
        post.setContent(content);
        postRepository.save(post);

        return "<p style='color: green;'>Post updated successfully!</p>";
    }

    @PreAuthorize("hasRole('AUTHOR')")
    @GetMapping("/writepost")
    public String writepost() {
        return "writepost";
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // This maps to resources/templates/login.html
    }
    
}
