package com.arun.demo.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {
    private final JobLauncher jobLauncher;

    private final Job partitionedJob;
    private final Job mydemojob;

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES) //20s
    public void runBatch() {
        try{
            jobLauncher.run(partitionedJob,
                    new JobParametersBuilder()
                            .addString("runDate", LocalDateTime.now().toString()).toJobParameters()
            );
        } catch (Exception exception) {
            //Job launchers throw large stacktraces - better catch them
            log.error("Failed to progress on the job due to exception", exception);
        }

    }
}
