package com.arun.demo.writer;

import com.arun.demo.entity.TestTable;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyCustomItemWriter implements ItemStreamWriter<TestTable> {
    StatefulRedisConnection<String,String> statefulRedisConnection;
    RedisAsyncCommands<String,String> asyncCommands;
    private final RedisClient demoRedisClient;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            statefulRedisConnection = demoRedisClient.connect();
            asyncCommands = statefulRedisConnection.async();
            log.info("Opened connections successfully");
        } catch (Exception exception) {
            statefulRedisConnection = null;
            asyncCommands = null;
            log.info("Failed to open Redis connections due to", exception);
        }

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        try{
            log.info("Closing connections...");
        } finally{
            if(asyncCommands != null){
                asyncCommands.flushCommands();
            }
            if(statefulRedisConnection != null) {
                statefulRedisConnection.close();
            }
            log.info("Closed connections successfully");
        }
    }

    @Override
    public void write(List<? extends TestTable> items) throws Exception {
        List<TestTable> tempItems = null;
        try{
            tempItems = (List<TestTable>) items;
            tempItems.forEach(x->  asyncCommands.sadd(String.valueOf(x.getId()),"yyyyy"));
            log.info("Pushed to redis items via ItemStreamWriter {}", tempItems.size());
        } finally {
            items = null;
            tempItems = null;
            log.info("Cleared items successfully");
        }
    }
}
