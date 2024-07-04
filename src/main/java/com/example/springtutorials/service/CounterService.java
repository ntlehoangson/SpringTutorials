package com.example.springtutorials.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CounterService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_KEY = "counter_lock";
    private static final String COUNTER_KEY = "counter";

    public void incrementCounter() {
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, 1, 5, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(lockAcquired)) {
            try {
                Integer counter = (Integer) redisTemplate.opsForValue().get(COUNTER_KEY);
                if (counter == null) {
                    counter = 0;
                }
                counter++;
                redisTemplate.opsForValue().set(COUNTER_KEY, counter);
                System.out.println("Current counter is " + counter);
            } finally {
                redisTemplate.delete(LOCK_KEY);
                System.out.println("Unlock success!");
            }
        } else {
            System.out.println("Failed to acquire lock");
        }
    }
}