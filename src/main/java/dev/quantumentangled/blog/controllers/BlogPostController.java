package dev.quantumentangled.blog.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.quantumentangled.blog.entities.BlogPost;
import dev.quantumentangled.blog.repositories.BlogPostRepository;
import dev.quantumentangled.blog.services.MarkdownService;

@RestController
@RequestMapping("/posts")
@PreAuthorize("hasRole('AUTHOR')")
public class BlogPostController {

    private final BlogPostRepository repo;
    private final MarkdownService markdownService;

    public BlogPostController(BlogPostRepository repo, MarkdownService markdownService) {
        this.repo = repo;
        this.markdownService = markdownService;
    }

    @PostMapping
    public ResponseEntity<?> savePost(@RequestParam String title, @RequestParam String content) {
        BlogPost post = new BlogPost();
        post.setTitle(title);
        post.setContent(content);
        repo.save(post);
        return ResponseEntity.ok("Saved");
    }

    @PostMapping("/preview")
    public String previewMarkdown(@RequestParam String content) {
        return markdownService.renderToHtml(content);
    }
}
