package com.example.demo.config.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile("dev")
@Slf4j
public class LoggingAspect {

  @Around(
      "@annotation(com.example.demo.config.logging.Loggable) || @within(com.example.demo.config.logging.Loggable)")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    log.debug("{}.{}({} args)", className, methodName, args.length);
    long start = System.currentTimeMillis();

    try {
      Object result = joinPoint.proceed();
      log.debug("{}.{} → ({}ms)", className, methodName, System.currentTimeMillis() - start);
      return result;
    } catch (Exception e) {
      log.warn(
          "{}.{} failed: {} ({}ms)",
          className,
          methodName,
          e.getMessage(),
          System.currentTimeMillis() - start);
      throw e;
    }
  }
}
