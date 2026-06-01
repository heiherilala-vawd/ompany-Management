package com.example.demo.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.model.Company;
import com.example.demo.model.Job;
import com.example.demo.model.User;
import com.example.demo.model.exception.BadRequestException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoreValidatorTest {

  private CoreValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CoreValidator();
  }

  @Test
  void validateCompany_valid_doesNotThrow() {
    assertThatCode(() -> validator.validateCompany(new Company())).doesNotThrowAnyException();
  }

  @Test
  void validateCompany_null_throws() {
    assertThatThrownBy(() -> validator.validateCompany(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Company cannot be null");
  }

  @Test
  void validateCompanies_null_throws() {
    assertThatThrownBy(() -> validator.validateCompanies(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Company list cannot be null or empty");
  }

  @Test
  void validateCompanies_empty_throws() {
    assertThatThrownBy(() -> validator.validateCompanies(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Company list cannot be null or empty");
  }

  @Test
  void validateCompanies_valid_doesNotThrow() {
    assertThatCode(() -> validator.validateCompanies(List.of(new Company())))
        .doesNotThrowAnyException();
  }

  @Test
  void validateUser_valid_doesNotThrow() {
    assertThatCode(() -> validator.validateUser(new User())).doesNotThrowAnyException();
  }

  @Test
  void validateUser_null_throws() {
    assertThatThrownBy(() -> validator.validateUser(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User cannot be null");
  }

  @Test
  void validateUsers_null_throws() {
    assertThatThrownBy(() -> validator.validateUsers(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User list cannot be null or empty");
  }

  @Test
  void validateUsers_empty_throws() {
    assertThatThrownBy(() -> validator.validateUsers(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("User list cannot be null or empty");
  }

  @Test
  void validateUsers_valid_doesNotThrow() {
    assertThatCode(() -> validator.validateUsers(List.of(new User()))).doesNotThrowAnyException();
  }

  @Test
  void validateJob_valid_doesNotThrow() {
    Job job =
        Job.builder()
            .id("job1")
            .company(Company.builder().id("c1").build())
            .startDate(LocalDate.of(2024, 1, 1))
            .endDate(LocalDate.of(2024, 12, 31))
            .build();
    assertThatCode(() -> validator.validateJob(job)).doesNotThrowAnyException();
  }

  @Test
  void validateJob_null_throws() {
    assertThatThrownBy(() -> validator.validateJob(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job cannot be null");
  }

  @Test
  void validateJob_companyNull_throws() {
    Job job = Job.builder().id("job1").company(null).build();
    assertThatThrownBy(() -> validator.validateJob(job))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job must be associated with a company");
  }

  @Test
  void validateJob_companyWithoutId_throws() {
    Job job = Job.builder().id("job1").company(new Company()).build();
    assertThatThrownBy(() -> validator.validateJob(job))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job must be associated with a company");
  }

  @Test
  void validateJob_endDateBeforeStartDate_throws() {
    Job job =
        Job.builder()
            .id("job1")
            .company(Company.builder().id("c1").build())
            .startDate(LocalDate.of(2024, 12, 31))
            .endDate(LocalDate.of(2024, 1, 1))
            .build();
    assertThatThrownBy(() -> validator.validateJob(job))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job end date cannot be before start date");
  }

  @Test
  void validateJob_allFieldsInvalid_throws() {
    Job job =
        Job.builder()
            .id("job1")
            .company(null)
            .startDate(LocalDate.of(2024, 12, 31))
            .endDate(LocalDate.of(2024, 1, 1))
            .build();
    assertThatThrownBy(() -> validator.validateJob(job))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job must be associated with a company")
        .hasMessageContaining("Job end date cannot be before start date");
  }

  @Test
  void validateJobs_null_throws() {
    assertThatThrownBy(() -> validator.validateJobs(null))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job list cannot be null or empty");
  }

  @Test
  void validateJobs_empty_throws() {
    assertThatThrownBy(() -> validator.validateJobs(Collections.emptyList()))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Job list cannot be null or empty");
  }

  @Test
  void validateJobs_valid_doesNotThrow() {
    Job job = Job.builder().id("job1").company(Company.builder().id("c1").build()).build();
    assertThatCode(() -> validator.validateJobs(List.of(job))).doesNotThrowAnyException();
  }
}
