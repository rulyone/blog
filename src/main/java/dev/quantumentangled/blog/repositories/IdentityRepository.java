package dev.quantumentangled.blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.quantumentangled.blog.entities.Identity;

public interface IdentityRepository extends JpaRepository<Identity, Long> {
    Optional<Identity> findByProviderAndProviderId(String provider, String providerId);
}