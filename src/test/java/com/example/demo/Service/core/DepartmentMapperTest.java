package com.example.demo.Service.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.demo.client.model.CrupdateCompany;
import com.example.demo.client.model.CrupdateDepartment;
import com.example.demo.client.model.Department;
import com.example.demo.endpoint.rest.mapper.CompanyMapper;
import com.example.demo.endpoint.rest.mapper.core.DepartmentMapper;
import com.example.demo.model.Company;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DepartmentMapperTest {

  @Mock private CompanyMapper companyMapper;

  private DepartmentMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DepartmentMapper(companyMapper);
  }

  @Test
  void toDomain_ShouldMapAllFields() {
    CrupdateDepartment rest =
        new CrupdateDepartment()
            .id("dept-1")
            .name("Engineering")
            .description("Engineering department")
            .companyId("comp-1")
            .comment("Test comment");

    com.example.demo.model.core.Department result = mapper.toDomain(rest, "comp-1");

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("dept-1");
    assertThat(result.getName()).isEqualTo("Engineering");
    assertThat(result.getDescription()).isEqualTo("Engineering department");
    assertThat(result.getCompany()).isNotNull();
    assertThat(result.getCompany().getId()).isEqualTo("comp-1");
    assertThat(result.getComment()).isEqualTo("Test comment");
  }

  @Test
  void toDomain_ShouldReturnNull_WhenInputIsNull() {
    com.example.demo.model.core.Department result = mapper.toDomain(null, "comp-1");
    assertThat(result).isNull();
  }

  @Test
  void toDomain_ShouldHandleNullCompanyId() {
    CrupdateDepartment rest = new CrupdateDepartment().id("dept-1").name("Engineering");

    com.example.demo.model.core.Department result = mapper.toDomain(rest, null);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("dept-1");
    assertThat(result.getCompany()).isNull();
  }

  @Test
  void toDomain_ShouldHandleNullId() {
    CrupdateDepartment rest =
        new CrupdateDepartment().id(null).name("New Dept").companyId("comp-1");

    com.example.demo.model.core.Department result = mapper.toDomain(rest, "comp-1");

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNull();
    assertThat(result.getName()).isEqualTo("New Dept");
  }

  @Test
  void toDomain_ShouldHandleMinimalFields() {
    CrupdateDepartment rest = new CrupdateDepartment().id("dept-1").name("Minimal");

    com.example.demo.model.core.Department result = mapper.toDomain(rest, null);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("dept-1");
    assertThat(result.getName()).isEqualTo("Minimal");
    assertThat(result.getDescription()).isNull();
    assertThat(result.getCompany()).isNull();
    assertThat(result.getComment()).isNull();
  }

  @Test
  void toRestDepartment_ShouldMapAllFields() {
    Company company = Company.builder().id("comp-1").build();
    com.example.demo.model.core.Department domain =
        com.example.demo.model.core.Department.builder()
            .id("dept-1")
            .name("Engineering")
            .description("Engineering department")
            .company(company)
            .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
            .updatedAt(Instant.parse("2024-06-01T00:00:00Z"))
            .comment("Test comment")
            .build();

    when(companyMapper.toRestCrupdateCompany(company))
        .thenReturn(new CrupdateCompany().id("comp-1"));

    Department result = mapper.toRestDepartment(domain);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("dept-1");
    assertThat(result.getName()).isEqualTo("Engineering");
    assertThat(result.getDescription()).isEqualTo("Engineering department");
    assertThat(result.getCompany()).isNotNull();
    assertThat(result.getCompany().getId()).isEqualTo("comp-1");
    assertThat(result.getCreatedAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    assertThat(result.getUpdatedAt()).isEqualTo(Instant.parse("2024-06-01T00:00:00Z"));
    assertThat(result.getComment()).isEqualTo("Test comment");
  }

  @Test
  void toRestDepartment_ShouldReturnNull_WhenInputIsNull() {
    Department result = mapper.toRestDepartment(null);
    assertThat(result).isNull();
  }

  @Test
  void toRestDepartment_ShouldHandleNullCompany() {
    com.example.demo.model.core.Department domain =
        com.example.demo.model.core.Department.builder()
            .id("dept-1")
            .name("Engineering")
            .company(null)
            .build();

    Department result = mapper.toRestDepartment(domain);

    assertThat(result).isNotNull();
    assertThat(result.getCompany()).isNull();
  }

  @Test
  void toRestDepartment_ShouldHandleNullTimestamps() {
    com.example.demo.model.core.Department domain =
        com.example.demo.model.core.Department.builder()
            .id("dept-1")
            .name("Engineering")
            .company(Company.builder().id("comp-1").build())
            .createdAt(null)
            .updatedAt(null)
            .build();

    Department result = mapper.toRestDepartment(domain);

    assertThat(result).isNotNull();
    assertThat(result.getCreatedAt()).isNull();
    assertThat(result.getUpdatedAt()).isNull();
  }

  @Test
  void toRestDepartment_ShouldHandleNullDescription() {
    com.example.demo.model.core.Department domain =
        com.example.demo.model.core.Department.builder()
            .id("dept-1")
            .name("Engineering")
            .description(null)
            .company(Company.builder().id("comp-1").build())
            .build();

    Department result = mapper.toRestDepartment(domain);

    assertThat(result).isNotNull();
    assertThat(result.getDescription()).isNull();
  }

  @Test
  void toRestDepartments_ShouldMapList() {
    com.example.demo.model.core.Department domain =
        com.example.demo.model.core.Department.builder().id("dept-1").name("Engineering").build();

    List<Department> result = mapper.toRestDepartments(List.of(domain));

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo("dept-1");
  }

  @Test
  void toRestDepartments_ShouldReturnEmpty_WhenInputIsEmpty() {
    List<Department> result = mapper.toRestDepartments(List.of());
    assertThat(result).isEmpty();
  }

  @Test
  void toRestDepartments_ShouldHandleMultipleDepartments() {
    com.example.demo.model.core.Department dept1 =
        com.example.demo.model.core.Department.builder().id("dept-1").name("Engineering").build();
    com.example.demo.model.core.Department dept2 =
        com.example.demo.model.core.Department.builder().id("dept-2").name("HR").build();

    List<Department> result = mapper.toRestDepartments(List.of(dept1, dept2));

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getId()).isEqualTo("dept-1");
    assertThat(result.get(1).getId()).isEqualTo("dept-2");
  }
}
