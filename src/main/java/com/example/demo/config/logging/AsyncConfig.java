package com.example.demo.config.logging;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  private final CorrelationIdTaskDecorator taskDecorator;

  public AsyncConfig(CorrelationIdTaskDecorator taskDecorator) {
    this.taskDecorator = taskDecorator;
  }

  @Bean(destroyMethod = "shutdown")
  public ThreadPoolTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(100);
    executor.setTaskDecorator(taskDecorator);
    executor.setThreadNamePrefix("async-");
    executor.initialize();
    return executor;
  }

  @Override
  public Executor getAsyncExecutor() {
    return taskExecutor();
  }
}
