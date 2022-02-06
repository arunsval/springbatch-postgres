package com.arun.demo.config;

import com.arun.demo.entity.TestTable;
import com.arun.demo.listener.IWriteListener;
import com.arun.demo.repository.TestTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final StepBuilderFactory stepBuilderFactory;
    private final JobBuilderFactory jobBuilderFactory;
    private final TestTableRepository testTableRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final IWriteListener itemWriteListener;

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .chunk(1000)
                .reader(repositoryItemReader())
//                .processor(null)
               .writer(listItemWriter())
                .listener(itemWriteListener)
                .build();
    }

    @Bean
    public Job job1() {
        return jobBuilderFactory.get("job1"+System.currentTimeMillis())
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .build()
                .build();
    }


    @Bean
    public JpaPagingItemReader jpaPagingItemReader(){
        return new JpaPagingItemReaderBuilder<TestTable>().name("testtable")
                .pageSize(1000)
                .queryString("FROM TestTable")
                .entityManagerFactory(entityManagerFactory)
                .build();
    }


    @Bean
    public RepositoryItemReader repositoryItemReader() {
        RepositoryItemReader<TestTable> recordsReader = new RepositoryItemReader<>();
        recordsReader.setPageSize(100);
        recordsReader.setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        recordsReader.setRepository(testTableRepository);
        recordsReader.setMethodName("findAll");
        return recordsReader;
    }

    @Bean
    public ItemWriter listItemWriter(){
        return new ListItemWriter();
    }


}
