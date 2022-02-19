package com.arun.demo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "activemq")
@Data
@Slf4j
public class ActiveMQBrokerConfig {
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory = null;
        try{
            activeMQConnectionFactory = new ActiveMQConnectionFactory();
            activeMQConnectionFactory.setTrustAllPackages(true);
//            Collections.singletonList("com.arun.demo"));
            activeMQConnectionFactory.setBrokerURL(brokerUrl);

        } catch (Exception exception){
            log.error("Failed to create ActiveMQ Connection", exception);
        }
        return activeMQConnectionFactory;
    }
}
