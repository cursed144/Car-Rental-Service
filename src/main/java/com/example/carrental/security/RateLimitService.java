package com.example.carrental.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    public boolean isAllowed(String key, int maxRequests, long windowSeconds) {
        long now = Instant.now().getEpochSecond();

        RateLimitBucket bucket = buckets.compute(key, (k, existing) -> {
            if (existing == null || now >= existing.windowStart + windowSeconds) {
                return new RateLimitBucket(1, now);
            }

            existing.requestCount++;
            return existing;
        });

        cleanupOldEntries(windowSeconds, now);

        return bucket.requestCount <= maxRequests;
    }

    private void cleanupOldEntries(long windowSeconds, long now) {
        if (buckets.size() > 10_000) {
            buckets.entrySet().removeIf(entry ->
                    now >= entry.getValue().windowStart + (windowSeconds * 2)
            );
        }
    }

    private static class RateLimitBucket {
        private int requestCount;
        private final long windowStart;

        private RateLimitBucket(int requestCount, long windowStart) {
            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }
    }
}