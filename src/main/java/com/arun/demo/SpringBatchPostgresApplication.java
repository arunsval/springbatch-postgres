package com.arun.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SpringBatchPostgresApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPostgresApplication.class, args);
    }




   /* @EventListener(ApplicationReadyEvent.class)
    public void checkDB(){
        RedisURI redisURI = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withDatabase(0)
                .build();
        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection statefulRedisConnection = redisClient.connect();
        RedisAsyncCommands<String,String> asyncCommands =statefulRedisConnection.async();
        asyncCommands.set("hhh","yyyyy");
        statefulRedisConnection.close();
        redisClient.shutdown();
    }*/
}
