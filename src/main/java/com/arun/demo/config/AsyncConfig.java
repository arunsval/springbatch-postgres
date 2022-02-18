package com.arun.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "async")
@Data
public class AsyncConfig {
    private int coreSize;
    private int maxSize;
}
