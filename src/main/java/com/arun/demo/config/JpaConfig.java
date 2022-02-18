package com.arun.demo.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = "com.arun.demo.repository")
@RequiredArgsConstructor
public class JpaConfig {
    private final SessionFactory sessionFactory;
    private final EntityManagerFactory entityManagerFactory;

    @PreDestroy
    public void close(){
        sessionFactory.close();
        entityManagerFactory.close();
    }

}
