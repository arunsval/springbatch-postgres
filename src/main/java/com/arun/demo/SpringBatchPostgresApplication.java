package com.arun.demo;

import com.arun.demo.repository.TestTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SpringBatchPostgresApplication {
//    private final TestTableRepository testTableRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchPostgresApplication.class, args);
    }

   /* @EventListener(ApplicationReadyEvent.class)
    public void checkDB(){
        log.info("Found rows: {}", testTableRepository.findAll().stream().count());
    }*/
}
