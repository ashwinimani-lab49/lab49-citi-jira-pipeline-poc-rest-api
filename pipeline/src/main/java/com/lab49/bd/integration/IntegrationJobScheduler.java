package com.lab49.bd.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class IntegrationJobScheduler {

  private final String SCHEDULE_ID = "scheduleId";
  private final JobLauncher jobLauncher;
  private final Job job;

  public IntegrationJobScheduler(JobLauncher jobLauncher, Job job) {
    this.jobLauncher = jobLauncher;
    this.job = job;
  }

  @Scheduled(cron = "${job.schedule.cron.expression}")
  public void perform() throws Exception {
    JobParameters params = new JobParametersBuilder()
        .addString(SCHEDULE_ID, String.valueOf(System.currentTimeMillis()))
        .toJobParameters();
    jobLauncher.run(job, params);
  }
}
