package com.example.demo.config.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class CorrelationIdTaskDecoratorTest {

  private CorrelationIdTaskDecorator decorator;

  @BeforeEach
  void setUp() {
    decorator = new CorrelationIdTaskDecorator();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void decorate_ShouldPropagateMdc_ToWrappedTask() throws Exception {
    MDC.put("correlationId", "test-id");
    MDC.put("userId", "user-123");

    AtomicReference<String> capturedCorrelationId = new AtomicReference<>();
    AtomicReference<String> capturedUserId = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    Runnable wrapped = decorator.decorate(() -> {
      capturedCorrelationId.set(MDC.get("correlationId"));
      capturedUserId.set(MDC.get("userId"));
      latch.countDown();
    });

    new Thread(wrapped).start();
    latch.await();

    assertThat(capturedCorrelationId.get()).isEqualTo("test-id");
    assertThat(capturedUserId.get()).isEqualTo("user-123");
  }

  @Test
  void decorate_ShouldNotAffectCallerThreadMdc() throws Exception {
    MDC.put("correlationId", "caller-thread");

    CountDownLatch latch = new CountDownLatch(1);
    Runnable wrapped = decorator.decorate(() -> latch.countDown());

    new Thread(wrapped).start();
    latch.await();

    assertThat(MDC.get("correlationId")).isEqualTo("caller-thread");
  }

  @Test
  void decorate_ShouldHandleNullContextMap() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    Runnable wrapped = decorator.decorate(() -> latch.countDown());

    assertThatCode(() -> {
      new Thread(wrapped).start();
      latch.await();
    }).doesNotThrowAnyException();
  }
}
