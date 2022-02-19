package com.arun.demo.config;

import com.arun.demo.entity.TestTable;
import com.arun.demo.reader.MyCustomItemReader;
import com.arun.demo.writer.MyCustomItemWriter;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@EnableBatchIntegration
@Import(ActiveMQBrokerConfig.class)
@Profile("worker")
public class RemotePartitionedJobWorkerConfig {
    private final RemotePartitioningWorkerStepBuilderFactory workerStepBuilderFactory;
    private final MyCustomItemReader myCustomReader;
    private final MyCustomItemWriter myCustomWriter;
    private final AsyncConfig asyncConfig;
    private final JobConfig jobConfig;

    @Bean
    public MessageChannel requestsChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow(ActiveMQConnectionFactory activeMQConnectionFactory){
        return IntegrationFlows.from(Jms.messageDrivenChannelAdapter(activeMQConnectionFactory).destination("requests"))
                .channel(requestsChannel())
                .get();
    }

    @Bean
    public Step workerStep(){
        return workerStepBuilderFactory.get("workerStep")
                .inputChannel(requestsChannel())
                .<TestTable,TestTable>chunk(jobConfig.getChunkSize())
                .reader(myCustomReader.jpaPagingItemReaderWithRange(0,0,0))
                .writer(myCustomWriter)
                .taskExecutor(threadPoolTaskExecutor())
                .build();
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
