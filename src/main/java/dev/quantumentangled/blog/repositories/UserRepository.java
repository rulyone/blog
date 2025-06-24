package dev.quantumentangled.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.quantumentangled.blog.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}