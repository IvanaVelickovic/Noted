package com.Noted.integration;

import com.Noted.service.RateLimitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RateLimitTest {

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Long TEST_USER_ID = 999999L;
    private static final Long TEST_USER_ID_2 = 888888L;

    @AfterEach
    void cleanUp() {
        redisTemplate.delete("ratelimit:summarize:" + TEST_USER_ID);
        redisTemplate.delete("ratelimit:summarize:" + TEST_USER_ID_2);
    }

    @Test
    void allowsRequestsUpToLimit() {
        int max = 5;
        Duration window = Duration.ofSeconds(10);

        for (int i = 1; i <= max; i++) {
            assertTrue(rateLimitService.isAllowed(TEST_USER_ID, max, window),
                    "Request " + i + " should be allowed");
        }
    }

    @Test
    void blocksRequestsOverLimit() {
        int max = 5;
        Duration window = Duration.ofSeconds(10);

        for (int i = 1; i <= max; i++) {
            rateLimitService.isAllowed(TEST_USER_ID, max, window);
        }

        // 6th request should be blocked
        assertFalse(rateLimitService.isAllowed(TEST_USER_ID, max, window));
    }

    @Test
    void windowResetsAfterExpiry() throws InterruptedException {
        int max = 2;
        Duration window = Duration.ofSeconds(2); // short window just for this test

        assertTrue(rateLimitService.isAllowed(TEST_USER_ID, max, window)); // 1st
        assertTrue(rateLimitService.isAllowed(TEST_USER_ID, max, window)); // 2nd
        assertFalse(rateLimitService.isAllowed(TEST_USER_ID, max, window)); // 3rd, blocked

        Thread.sleep(2500); // wait past the window

        assertTrue(rateLimitService.isAllowed(TEST_USER_ID, max, window),
                "Should be allowed again after window expires");
    }

    @Test
    void limitsAreIndependentPerUser() {
        int max = 2;
        Duration window = Duration.ofSeconds(10);

        assertTrue(rateLimitService.isAllowed(TEST_USER_ID, max, window));
        assertTrue(rateLimitService.isAllowed(TEST_USER_ID, max, window));
        assertFalse(rateLimitService.isAllowed(TEST_USER_ID, max, window)); // user 1 maxed out

        // user 2 should be unaffected
        assertTrue(rateLimitService.isAllowed(TEST_USER_ID_2, max, window));
    }
}
