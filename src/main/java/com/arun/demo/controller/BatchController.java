package com.arun.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class BatchController {

    @GetMapping("/pushToRedis")
    public Flux<String> pushToRedis() {
        return Flux.empty();
    }
}
