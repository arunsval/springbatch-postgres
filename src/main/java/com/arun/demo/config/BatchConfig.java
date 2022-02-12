package com.arun.demo.config;

import com.arun.demo.entity.TestTable;
import com.arun.demo.reader.MyCustomItemReader;
import com.arun.demo.writer.MyCustomItemWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final MyCustomItemReader myCustomReader;
    private final MyCustomItemWriter myCustomWriter;


    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<TestTable,TestTable>chunk(10000)
                .reader(myCustomReader.hibernatePagingItemReader())
                .writer(myCustomWriter)
               .taskExecutor(threadPoolTaskExecutor())
                .build();
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
       ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
       executor.setCorePoolSize(100); //don't create more threads than your allowed postgres db connections
       executor.setThreadNamePrefix("Multithread");
       return executor;
    }


    @Bean("mydemojob")
    public Job job1() {
        return jobBuilderFactory.get("job1"+System.currentTimeMillis())
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .build()
                .build();
    }




}
