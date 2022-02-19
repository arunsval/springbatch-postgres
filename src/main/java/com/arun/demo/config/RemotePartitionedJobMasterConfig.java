package com.arun.demo.config;

import com.arun.demo.partitioner.MyCustomPartitioner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.integration.config.annotation.BatchIntegrationConfiguration;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableIntegration
@EnableScheduling
@Import({BatchIntegrationConfiguration.class, ActiveMQBrokerConfig.class})
@Profile("master")
@Slf4j
public class RemotePartitionedJobMasterConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final RemotePartitioningManagerStepBuilderFactory managerStepBuilderFactory;
    private final MyCustomPartitioner myCustomPartitioner;
    private final JobConfig jobConfig;
    private final JobLauncher jobLauncher;

    @Bean
    public MessageChannel requestsChannel(){
        return new DirectChannel();
    }


    @Bean
    public Job remotePartitionedJob(){
        return jobBuilderFactory.get("remotePartitionedJob")
                .start(remoteMasterStep())
                .build();

    }

    @Bean
    public Step remoteMasterStep() {
        return managerStepBuilderFactory.get("remoteMasterStep")
                .partitioner("workerStep",myCustomPartitioner)
                .gridSize(jobConfig.getGridSize())
                .outputChannel(requestsChannel())
                .build();
    }

    @Bean
    public IntegrationFlow outboundFlow(ActiveMQConnectionFactory activeMQConnectionFactory){
        return IntegrationFlows.from(requestsChannel())
                .handle(Jms.outboundAdapter(activeMQConnectionFactory).destination("requests"))
                .get();
    }


    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void runBatch() {
        try{
            jobLauncher.run(remotePartitionedJob(),
                    new JobParametersBuilder()
                            .addString("runDate", LocalDateTime.now().toString()).toJobParameters()
            );
        } catch (Exception exception) {
            //Job launchers throw large stacktraces - better catch them
            log.error("Failed to progress on the job due to exception", exception);
        }

    }

}
