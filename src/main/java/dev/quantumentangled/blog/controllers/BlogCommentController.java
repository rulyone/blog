package dev.quantumentangled.blog.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.quantumentangled.blog.entities.BlogComment;
import dev.quantumentangled.blog.entities.BlogPost;
import dev.quantumentangled.blog.entities.User;
import dev.quantumentangled.blog.repositories.BlogCommentRepository;
import dev.quantumentangled.blog.repositories.BlogPostRepository;
import dev.quantumentangled.blog.repositories.UserRepository;

@Controller
@PreAuthorize("isAuthenticated()")
public class BlogCommentController {

    private final BlogCommentRepository commentRepo;
    private final BlogPostRepository postRepo;
    private final UserRepository userRepo;

    public BlogCommentController(BlogCommentRepository commentRepository, BlogPostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepo = commentRepository;
        this.postRepo = postRepository;
        this.userRepo = userRepository;
    }

    @PostMapping("posts/{id}/{slug}/comments")
    public String addComment(@PathVariable Long id,
            @PathVariable String slug,
            @RequestParam String content,
            @AuthenticationPrincipal OAuth2User oauth2User,
            Model model) {

        String githubId = oauth2User.getAttribute("login");
        BlogPost post = postRepo.findById(id).orElseThrow();
        User user = userRepo.findByUsername(githubId).orElseThrow();

        BlogComment comment = new BlogComment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(user);
        commentRepo.save(comment);

        List<BlogComment> comments = commentRepo.findByPostIdOrderByCreatedAtAsc(post.getId());
        model.addAttribute("comments", comments);

        return "fragments/comments :: commentList";
    }
}
