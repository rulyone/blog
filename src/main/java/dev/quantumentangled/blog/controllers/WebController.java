package dev.quantumentangled.blog.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import dev.quantumentangled.blog.entities.BlogComment;
import dev.quantumentangled.blog.entities.BlogPost;
import dev.quantumentangled.blog.repositories.BlogCommentRepository;
import dev.quantumentangled.blog.repositories.BlogPostRepository;
import dev.quantumentangled.blog.services.MarkdownService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class WebController {

    private final BlogPostRepository postRepository;
    private final BlogCommentRepository commentRepository;
    private final MarkdownService markdownService;

    private final int DEFAULT_PAGE_SIZE = 1;

    private final RequestCache requestCache = new HttpSessionRequestCache();

    public WebController(BlogPostRepository postRepository, BlogCommentRepository commentRepository, MarkdownService markdownService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.markdownService = markdownService;
    }

    @GetMapping("/")
    public String blogPage(HttpServletRequest request, HttpServletResponse response, 
                            Authentication authentication, Model model) {

        PageRequest pageRequest = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<BlogPost> posts = postRepository.findAll(pageRequest);

        // Convert Markdown to HTML for each post
        List<Map<String, Object>> renderedPosts = posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("slug", post.getSlug());
            map.put("title", post.getTitle());
            map.put("htmlContent", markdownService.renderToHtml(post.getContent()));
            map.put("createdAt", post.getCreatedAt());
            return map;
        }).toList();

        model.addAttribute("posts", renderedPosts);
        model.addAttribute("hasNext", posts.hasNext());
        model.addAttribute("nextPage", 1);

        if (authentication == null) {
            requestCache.saveRequest(request, response);
        }
        return "blog";
    }

    @GetMapping("/posts")
    public String loadMorePosts(@RequestParam(defaultValue = "1") int page,
                               Model model,
                               HttpServletResponse response) {

        PageRequest pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogPost> postPage = postRepository.findAll(pageRequest);

        if (postPage.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return null;
        }

        // Convert markdown for this page
        List<Map<String, Object>> renderedPosts = postPage.getContent().stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("slug", post.getSlug());
            map.put("title", post.getTitle());
            map.put("htmlContent", markdownService.renderToHtml(post.getContent()));
            map.put("createdAt", post.getCreatedAt());
            return map;
        }).toList();

        model.addAttribute("posts", renderedPosts);
        model.addAttribute("hasNext", postPage.hasNext());
        model.addAttribute("nextPage", page + 1);

        return "fragments/posts :: postList";
    }

    @GetMapping("/viewpost/{id}/{slug}")
    public String viewPost(HttpServletRequest request, HttpServletResponse response, 
                        Authentication authentication,
                        @PathVariable Long id,
                        @PathVariable String slug,
                        Model model) {
        BlogPost post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<BlogComment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());

        // Optional: redirect to canonical slug if changed
        if (!slug.equals(post.getSlug())) {
            return "redirect:/viewpost/" + post.getId() + "/" + post.getSlug();
        }

        String htmlContent = markdownService.renderToHtml(post.getContent());
        model.addAttribute("post", post);
        model.addAttribute("htmlContent", htmlContent);
        model.addAttribute("comments", comments);

        if (authentication == null) {
            requestCache.saveRequest(request, response);
        }
        return "viewpost"; 
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
        post.setSlugFromTitle(title);
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
        return "login"; 
    }
    
    @GetMapping("/portfolio")
    public String portfolio() {
        return "portfolio";
    }
    
    @GetMapping("/spatialindex")
    public String spatialIndex() {
        return "spatialindex";
    }
}
