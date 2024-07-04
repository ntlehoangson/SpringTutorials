package com.example.springtutorials.controller;

import com.example.springtutorials.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class CounterController {
    @Autowired
    private CounterService counterService;

    @GetMapping("/increment")
    public String increment() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> counterService.incrementCounter());
        }

        executorService.shutdown();
        return "Increment initiated";
    }
}
