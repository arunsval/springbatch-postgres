package com.arun.demo.config;

import com.arun.demo.entity.TestTable;
import com.arun.demo.partitioner.MyCustomPartitioner;
import com.arun.demo.reader.MyCustomItemReader;
import com.arun.demo.writer.MyCustomItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class LocalPartitionedBatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final MyCustomItemReader myCustomReader;
    private final MyCustomItemWriter myCustomWriter;
    private final MyCustomPartitioner myCustomPartitioner;
    private final AsyncConfig asyncConfig;
    private final JobConfig jobConfig;

    @Bean
    public Step masterStep(){
        return stepBuilderFactory.get("masterStep")
                .partitioner(slaveStep().getName(),myCustomPartitioner)
                .step(slaveStep())
                .gridSize(jobConfig.getGridSize())
                .taskExecutor(masterThreadPool())
                .build();
    }

    @Bean
    public Step slaveStep() {
        return stepBuilderFactory.get("slaveStep")
                .<TestTable,TestTable>chunk(30000)
                .reader(myCustomReader.jpaPagingItemReaderWithRange(0,0,0))
                .writer(myCustomWriter)
                .build();
    }



    @Bean
    public Job partitionedJob(){
        return jobBuilderFactory.get("partitionedJob")
                .start(masterStep())
                .build();
    }

    @Bean("masterThreadPool")
    public ThreadPoolTaskExecutor masterThreadPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);//don't create more threads than your allowed postgres db connections//
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("masterThreadPool");
        return executor;
    }

    @Bean("partitionerThreadPool")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncConfig.getCoreSize());//don't create more threads than your allowed postgres db connections//
        executor.setMaxPoolSize(asyncConfig.getMaxSize());
        executor.setThreadNamePrefix("partitionerThreadPool");
        return executor;
    }


}
