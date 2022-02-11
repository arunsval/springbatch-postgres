package com.arun.demo.listener;

import com.arun.demo.entity.TestTable;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class IWriteListener implements ItemWriteListener<TestTable> {
    private final RedisReactiveCommands redisReactiveCommands;
    private final RedisAsyncCommands redisAsyncCommands;
    private List<RedisFuture<Long>> itemsAdded;


    @Override
    public void beforeWrite(List<? extends TestTable> items) {

    }

    /***
     * NEED TO LIMIT THE CHUNK SIZE AS SMALL AS POSSIBLE TO REDUCE OOM ERRORS
     * @param items
     */
    @Override
    public void afterWrite(List<? extends TestTable> items) {
        IntStream.range(0,50000).forEach(
                x-> redisAsyncCommands.sadd(String.valueOf(x),"Hey")
        );
        redisAsyncCommands.flushCommands();

        log.info("Pushed to redis items {}", items);
    }

    @Override
    public void onWriteError(Exception exception, List<? extends TestTable> items) {
        log.error("Write failed due to {}", exception);
    }
}
