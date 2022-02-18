package com.arun.demo.reader;

import com.arun.demo.config.ReaderConfig;
import com.arun.demo.entity.TestTable;
import com.arun.demo.repository.TestTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.HibernatePagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.HibernateCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.HibernatePagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MyCustomItemReader {
    private final SessionFactory sessionFactory;
    private final TestTableRepository testTableRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final ReaderConfig readerConfig;

    @Bean
    public JpaPagingItemReader jpaPagingItemReader(){
        return new JpaPagingItemReaderBuilder<TestTable>().name("testtable")
                .pageSize(readerConfig.getPageSize())
                .queryString("FROM TestTable")
                .name("jpaPagingReader")
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<TestTable> jpaPagingItemReaderWithRange(
            @Value("#{stepExecutionContext[partitionNumber]}") int partitionNumber,
            @Value("#{stepExecutionContext[recordStart]}") int recordStart,
            @Value("#{stepExecutionContext[partitionPageSize]}") int partitionPageSize){
        log.info("partitionNumber {} reading from {} pageSize {}", partitionNumber, recordStart, partitionPageSize);
        return new JpaPagingItemReaderBuilder<TestTable>().name("testtable")
                .queryString("FROM TestTable")
                .name("jpaPaginReader")
                .currentItemCount(recordStart)
                .pageSize(partitionPageSize)
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
    public HibernateCursorItemReader hibernateCursorItemReader() {
        return new HibernateCursorItemReaderBuilder<TestTable>()
                .name("HibernateCursorReader")
                .fetchSize(0)
                .nativeQuery("SELECT ID,NAME FROM testtable1")
                .entityClass(TestTable.class)
                .sessionFactory(sessionFactory)
                .build();
    }

    @Bean
    public HibernatePagingItemReader hibernatePagingItemReader(){
        return new HibernatePagingItemReaderBuilder<TestTable>()
                .fetchSize(0)
                .pageSize(100000)
                .name("HibernatePagingItemReader")
                .queryString("FROM TestTable")
                .sessionFactory(sessionFactory)
                .build();
    }
}
