package dev.quantumentangled.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.quantumentangled.blog.entities.BlogPost;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
}