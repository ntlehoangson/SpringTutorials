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
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, 1, 10, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(lockAcquired)) {
            try {
                // Giả lập công việc lâu bằng Thread.sleep
                System.out.println(Thread.currentThread().getName() + " acquired lock, processing...");

                Thread.sleep(1);
                Integer counter = Integer.parseInt(redisTemplate.opsForValue().get(COUNTER_KEY).toString());
                if (counter == null) {
                    counter = 0;
                }
                counter++;
                redisTemplate.opsForValue().set(COUNTER_KEY, counter);
                System.out.println(Thread.currentThread().getName() + " current counter is " + counter);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted");
            } finally {
                redisTemplate.delete(LOCK_KEY);
                System.out.println(Thread.currentThread().getName() + " unlock success!");
            }
        } else {
            System.out.println(Thread.currentThread().getName() + " failed to acquire lock");
        }
    }
}
