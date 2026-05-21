package com.example.demo.integration.conf;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    String flywayTestdataPath = "classpath:/db/testdata";

    String image = System.getenv().getOrDefault("TC_POSTGRES_IMAGE", "postgres:15.2");
    String dbName = System.getenv().getOrDefault("TC_DB_NAME", "test-db");
    String dbUser = System.getenv().getOrDefault("TC_DB_USERNAME", "test");
    String dbPass = System.getenv().getOrDefault("TC_DB_PASSWORD", "test");
    String jwtSecret =
        System.getenv().getOrDefault("JWT_SECRET_KEY", "test-secret-key-test-secret-key-test");
    String jwtExpMs = System.getenv().getOrDefault("JWT_EXPIRATION_TIME", "86400000");

    PostgreSQLContainer<?> postgresContainer =
        new PostgreSQLContainer<>(image)
            .withDatabaseName(dbName)
            .withUsername(dbUser)
            .withPassword(dbPass);
    postgresContainer.start();

    TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        applicationContext,
        "server.port=" + this.getServerPort(),
        "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
        "spring.datasource.username=" + postgresContainer.getUsername(),
        "spring.datasource.password=" + postgresContainer.getPassword(),
        "spring.flyway.locations=classpath:/db/migration," + flywayTestdataPath,
        "jwt.secret.key=" + jwtSecret,
        "jwt.expiration.time=" + jwtExpMs);
  }

  public abstract int getServerPort();
}
