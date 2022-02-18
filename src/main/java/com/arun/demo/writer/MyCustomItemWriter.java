package com.arun.demo.writer;

import com.arun.demo.entity.TestTable;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@StepScope
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
            asyncCommands.setAutoFlushCommands(false);
//            log.info("thread {} Opened connections successfully",Thread.currentThread().getName());
        } catch (Exception exception) {
            statefulRedisConnection = null;
            asyncCommands = null;
            log.error("Failed to open Redis connections due to {}", exception.getLocalizedMessage());
        }

    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
       log.info("partition {} thread {} updating cache...",executionContext.get("partitionNumber"), Thread.currentThread().getName());
    }

    @Override
    public void close() throws ItemStreamException {
        try{
//            log.info("thread {} Closing connections...",Thread.currentThread().getName());
            asyncCommands.setAutoFlushCommands(true);
        } finally{
            if(asyncCommands != null){
                asyncCommands = null;
            }
            if(statefulRedisConnection != null) {
                statefulRedisConnection.close();
            }
//            log.info("thread {} Closed connections successfully",Thread.currentThread().getName());
        }
    }



    @Override
    public void write(List<? extends TestTable> items) throws Exception {
        List<TestTable> tempItems = null;
        try{
            tempItems = (List<TestTable>) items;
            tempItems.forEach(x->  asyncCommands.sadd(String.valueOf(x.getId()),"yyyyy"));
//            log.info("{} Pushed to redis items via ItemStreamWriter {}", Thread.currentThread().getName(),tempItems.size());
        } finally {
            asyncCommands.flushCommands();
            items = null;
            tempItems = null;
        }
    }
}
