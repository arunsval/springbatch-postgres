package com.arun.demo.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String,String> statefulRedisConnection(RedisClient redisClient){
        return redisClient.connect();
    }

    @Bean(destroyMethod = "shutdown")
    public RedisClient demoRedisClient(){
        RedisURI redisURI = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withDatabase(0)
                .build();
        RedisClient redisClient = RedisClient.create(redisURI);
        return redisClient;
    }

    @Bean
    RedisReactiveCommands redisReactiveCommands(StatefulRedisConnection statefulRedisConnection){
        return statefulRedisConnection.reactive();
    }

    @Bean
    RedisAsyncCommands redisAsyncCommands(StatefulRedisConnection statefulRedisConnection) {
        return statefulRedisConnection.async();
    }

}
