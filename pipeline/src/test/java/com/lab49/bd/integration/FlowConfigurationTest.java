package com.lab49.bd.integration;

import java.util.Collection;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {JobConfiguration.class, FlowConfiguration.class, BatchTestConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FlowConfigurationTest {
  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private JobRepositoryTestUtils jobRepositoryTestUtils;

  @After
  public void cleanUp() {
    jobRepositoryTestUtils.removeJobExecutions();
  }

  @Test
  public void whenStepTestJsonSchemaExecuted_thenSuccess() throws Exception {
    // Given
    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();
    // When
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("testJsonSchema", jobParameters);
    Collection stepExecutions = jobExecution.getStepExecutions();
    ExitStatus exitStatus = jobExecution.getExitStatus();
    // Then
    assertThat(stepExecutions.size(), is(1));
    assertThat(exitStatus.getExitCode(), is("COMPLETED"));
  }

  @Test
  public void whenStepCreateIssueInJiraExecuted_thenSuccess() throws Exception {
    // Given
    JobParameters jobParameters = jobLauncherTestUtils.getUniqueJobParameters();
    // When
    JobExecution jobExecution = jobLauncherTestUtils.launchStep("createIssueStep", jobParameters);
    Collection stepExecutions = jobExecution.getStepExecutions();
    ExitStatus exitStatus = jobExecution.getExitStatus();
    // Then
    assertThat(stepExecutions.size(), is(1));
    assertThat(exitStatus.getExitCode(), is("COMPLETED"));
  }
}