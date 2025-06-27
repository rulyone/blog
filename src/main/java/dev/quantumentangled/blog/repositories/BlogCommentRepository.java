package dev.quantumentangled.blog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.quantumentangled.blog.entities.BlogComment;

public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {
    
    List<BlogComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
