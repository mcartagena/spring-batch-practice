package com.springbatch.config;

import com.springbatch.decider.MyJobExecutionDecider;
import com.springbatch.listener.MyStepExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    @Bean
    public JobExecutionDecider decider(){
        return new MyJobExecutionDecider();
    }

    @Bean
    public StepExecutionListener myStepExecutionListener() {
        return new MyStepExecutionListener();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step1 executed!!s");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    boolean isError = false;
                    if (isError) {
                        throw new RuntimeException("step2 failed!!");
                    }
                    System.out.println("step2 executed!!s");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                //.listener(myStepExecutionListener())
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step3 executed!!s");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("step4 executed!!s");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job firstJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("job1", jobRepository)
                .start(step1(jobRepository, transactionManager))
                    .on("COMPLETED").to(decider())
                        .on("TEST_STATUS").to(step2(jobRepository, transactionManager))
                    .from(decider())
                        .on("*").to(step3(jobRepository, transactionManager))
                .end()
                .build();

        /* // Custom exit status example

        return new JobBuilder("job1", jobRepository)
                .start(step1(jobRepository, transactionManager))
                    .on("COMPLETED").to(step2(jobRepository, transactionManager))
                .from(step2(jobRepository, transactionManager))
                    //.on("COMPLETED").to(step3(jobRepository, transactionManager))
                    .on("TEST_STATUS").to(step3(jobRepository, transactionManager))
                .from(step2(jobRepository, transactionManager))
                    //.on("FAILED").to(step4(jobRepository, transactionManager))
                    .on("*").to(step4(jobRepository, transactionManager))
                .end()
                .build(); */
    }
}
