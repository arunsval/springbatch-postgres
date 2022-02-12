package com.arun.demo.listener;

import com.arun.demo.entity.TestTable;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IWriteListener implements ItemWriteListener<TestTable> {
    private final RedisReactiveCommands redisReactiveCommands;
    private final RedisAsyncCommands redisAsyncCommands;
    private List<RedisFuture<Long>> itemsAdded;
    private final RedisClient demoRedisClient;


    @Override
    public void beforeWrite(List<? extends TestTable> items) {

    }

    /***
     * NEED TO LIMIT THE CHUNK SIZE AS SMALL AS POSSIBLE TO REDUCE OOM ERRORS
     * @param items
     */
    @Override
    public void afterWrite(List<? extends TestTable> items) {
        StatefulRedisConnection statefulRedisConnection = demoRedisClient.connect();
        RedisAsyncCommands<String,String> asyncCommands = statefulRedisConnection.async();
        items.forEach(x->  asyncCommands.sadd(String.valueOf(x.getId()),"yyyyy"));
        asyncCommands.flushCommands();
        statefulRedisConnection.close();
        log.info("Pushed to redis items via listener {}", items.size());
    }

    @Override
    public void onWriteError(Exception exception, List<? extends TestTable> items) {
        log.error("Write failed due to {}", exception);
    }

    public void checkRedisDB(List<? extends TestTable> items){
        log.info("I'M PUSHING DATA TO REDIS");
        RedisURI redisURI = RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withDatabase(0)
                .build();
        RedisClient redisClient = RedisClient.create(redisURI);
        StatefulRedisConnection statefulRedisConnection = redisClient.connect();
        RedisAsyncCommands<String,String> asyncCommands =statefulRedisConnection.async();
        items.forEach(x->  asyncCommands.set(String.valueOf(x.getId()),"yyyyy"));

        statefulRedisConnection.close();

    }
}
