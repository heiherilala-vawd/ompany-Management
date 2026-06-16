package com.example.demo.integration.movement;

import static com.example.demo.integration.conf.TestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.SentryConf;
import com.example.demo.endpoint.rest.security.jwt.JwtUtils;
import com.example.demo.integration.conf.AbstractContextInitializer;
import com.example.demo.integration.conf.TestDataSqlLoader;
import com.example.demo.integration.conf.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = CarIT.ContextInitializer.class)
class CarIT {
  @Autowired private DataSource dataSource;
  @Autowired private MockMvc mockMvc;

  @MockitoBean private SentryConf sentryConf;
  @MockitoBean private AuthenticationManager authenticationManagerMock;
  @MockitoBean private JwtUtils jwtServiceMock;

  private static final ObjectMapper MAPPER =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .registerModule(new JsonNullableModule());

  @BeforeEach
  void setUp() throws Exception {
    TestUtils.setUpJwtService(jwtServiceMock);
    TestUtils.setUpAuthenticationManager(authenticationManagerMock);
    TestDataSqlLoader.executeAllSqlScripts(dataSource);
  }

  @Test
  void employee_can_get_car_by_id() throws Exception {
    mockMvc
        .perform(
            get(
                    "/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}",
                    ADMIN_ID,
                    COMPANY1_ID,
                    CAR1_EQUIPMENT_ID,
                    CAR1_WAREHOUSE_ID)
                .header("Authorization", "Bearer " + EMPLOYEE_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.equipment_id").value(CAR1_EQUIPMENT_ID))
        .andExpect(jsonPath("$.warehouse_id").value(CAR1_WAREHOUSE_ID))
        .andExpect(jsonPath("$.license_plate").value("AB-123-CD"));
  }

  @Test
  void user_with_bad_token_cannot_get_car_by_id() throws Exception {
    mockMvc
        .perform(
            get(
                    "/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}",
                    ADMIN_ID,
                    COMPANY1_ID,
                    CAR1_EQUIPMENT_ID,
                    CAR1_WAREHOUSE_ID)
                .header("Authorization", "Bearer " + BAD_TOKEN))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void employee_can_get_all_cars() throws Exception {
    mockMvc
        .perform(
            get("/users/{userId}/companies/{companyId}/cars", ADMIN_ID, COMPANY1_ID)
                .param("page", "1")
                .param("page_size", "100")
                .header("Authorization", "Bearer " + EMPLOYEE_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isArray());
  }

  @Test
  @DirtiesContext
  void administration_can_create_car() throws Exception {
    String body = MAPPER.writeValueAsString(java.util.List.of(someCreatableCar()));

    mockMvc
        .perform(
            put("/users/{userId}/companies/{companyId}/cars", ADMIN_ID, COMPANY1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("Authorization", "Bearer " + ADMINISTRATION_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].equipment_id").isString());
  }

  @Test
  @DirtiesContext
  void administration_can_update_car() throws Exception {
    var updatedCar = car1();
    updatedCar.setLicensePlate("ZZ-999-ZZ");
    String body = MAPPER.writeValueAsString(java.util.List.of(updatedCar));

    mockMvc
        .perform(
            put("/users/{userId}/companies/{companyId}/cars", ADMIN_ID, COMPANY1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("Authorization", "Bearer " + ADMINISTRATION_TOKEN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].license_plate").value("ZZ-999-ZZ"));
  }

  @Test
  void employee_cannot_create_car() throws Exception {
    String body = MAPPER.writeValueAsString(java.util.List.of(someCreatableCar()));

    mockMvc
        .perform(
            put("/users/{userId}/companies/{companyId}/cars", ADMIN_ID, COMPANY1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("Authorization", "Bearer " + EMPLOYEE_TOKEN))
        .andExpect(status().isForbidden());
  }

  @Test
  @DirtiesContext
  void admin_can_delete_car() throws Exception {
    mockMvc
        .perform(
            delete(
                    "/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}",
                    ADMIN_ID,
                    COMPANY1_ID,
                    CAR1_EQUIPMENT_ID,
                    CAR1_WAREHOUSE_ID)
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(
            get(
                    "/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}",
                    ADMIN_ID,
                    COMPANY1_ID,
                    CAR1_EQUIPMENT_ID,
                    CAR1_WAREHOUSE_ID)
                .header("Authorization", "Bearer " + ADMIN_TOKEN))
        .andExpect(status().isNotFound());
  }

  @Test
  void warehouse_worker_cannot_delete_car() throws Exception {
    mockMvc
        .perform(
            delete(
                    "/users/{userId}/companies/{companyId}/cars/{equipmentId}/{warehouseId}",
                    ADMIN_ID,
                    COMPANY1_ID,
                    CAR1_EQUIPMENT_ID,
                    CAR1_WAREHOUSE_ID)
                .header("Authorization", "Bearer " + WAREHOUSE_TOKEN))
        .andExpect(status().isForbidden());
  }

  static class ContextInitializer extends AbstractContextInitializer {
    public static final int SERVER_PORT = anAvailableRandomPort();

    @Override
    public int getServerPort() {
      return SERVER_PORT;
    }
  }
}
