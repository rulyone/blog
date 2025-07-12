package dev.quantumentangled.blog.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRateLimiter {
    private final Map<Long, Bucket> cache = new ConcurrentHashMap<>();
    
    public Bucket resolveBucket(Long userId) {
        return cache.computeIfAbsent(userId, k -> {
            Bandwidth limit = Bandwidth.builder()
                                .capacity(2)
                                .refillIntervally(2, Duration.ofDays(1))
                                .build();
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }
    
    public boolean tryConsume(Long userId) {
        return resolveBucket(userId).tryConsume(1);
    }
    
    public long getAvailableTokens(Long userId) {
        return resolveBucket(userId).getAvailableTokens();
    }
}