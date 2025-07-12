package dev.quantumentangled.blog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.quantumentangled.blog.entities.MailQueue;

@Repository
public interface MailQueueRepository extends JpaRepository<MailQueue, Long> {
    
    List<MailQueue> findByStatusAndAttemptsLessThan(MailQueue.Status status, Integer maxAttempts);
        
}