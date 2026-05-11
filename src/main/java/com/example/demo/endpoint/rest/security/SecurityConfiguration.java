package com.example.demo.endpoint.rest.security;

import static org.springframework.http.HttpMethod.*;

import com.example.demo.endpoint.rest.security.jwt.JwtAccessDeniedHandler;
import com.example.demo.endpoint.rest.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.endpoint.rest.security.jwt.JwtAuthenticationFilter;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint unauthorizedHandler;
  private final JwtAccessDeniedHandler accessDeniedHandler;
  private final UserService userService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .exceptionHandling(
            exception ->
                exception
                    .authenticationEntryPoint(unauthorizedHandler)
                    .accessDeniedHandler(accessDeniedHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // =========================
                    // AUTH & PUBLIC ENDPOINTS
                    // =========================
                    .requestMatchers("/auth/login", "/auth/register", "/ping")
                    .permitAll()
                    .requestMatchers("/auth/whoami")
                    .authenticated()

                    // =========================
                    // USERS
                    // =========================
                    .requestMatchers(GET, "/users", "/users/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users")
                    .authenticated()

                    // DELETE /users - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*")
                    .hasRole("ADMIN")

                    // =========================
                    // COMPANY
                    // =========================
                    .requestMatchers(GET, "/companies", "/companies/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies")
                    .authenticated()
                    // DELETE /companies - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*")
                    .hasRole("ADMIN")

                    // =========================
                    // JOB
                    // =========================
                    .requestMatchers(GET, "/companies/*/jobs", "/companies/*/jobs/*")
                    .authenticated()
                    .requestMatchers(GET, "/companies/*/jobs/*/users")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/users/*")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/jobs/*/users/*")
                    .authenticated()
                    // DELETE /jobs - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/jobs/*")
                    .hasRole("ADMIN")

                    // =========================
                    // WAREHOUSE
                    // =========================
                    .requestMatchers(GET, "/companies/*/warehouses", "/companies/*/warehouses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/warehouses")
                    .authenticated()
                    // DELETE /warehouses - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/warehouses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EQUIPMENT
                    // =========================
                    .requestMatchers(GET, "/companies/*/equipment", "/companies/*/equipment/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/equipment")
                    .authenticated()
                    // DELETE /equipment - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/equipment/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MATERIAL
                    // =========================
                    .requestMatchers(GET, "/materials", "/materials/*")
                    .authenticated()
                    // PUT /materials - ADMIN, ADMINISTRATION, WAREHOUSE_WORKER
                    .requestMatchers(PUT, "/materials")
                    .authenticated()
                    // DELETE /materials - ADMIN uniquement
                    .requestMatchers(DELETE, "/materials/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MATERIAL WAREHOUSE
                    // =========================
                    .requestMatchers(GET, "/companies/*/material_warehouse")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/material_warehouse")
                    .authenticated()

                    // =========================
                    // EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/expenses",
                        "/companies/*/job/*/user/*/expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/expenses")
                    .authenticated()
                    // DELETE /expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // INCOME
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/incomes",
                        "/companies/*/job/*/user/*/incomes/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/incomes")
                    .authenticated()
                    // DELETE /incomes - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/incomes/*")
                    .hasAnyRole("ADMIN", "WAREHOUSE_WORKER", "EMPLOYEE", "ADMINISTRATION")

                    // =========================
                    // INCOME RECEIPT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/incomes/*/receipts",
                        "/companies/*/job/*/user/*/incomes/*/receipts/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/incomes/*/receipts")
                    .authenticated()
                    // DELETE /receipts - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/incomes/*/receipts/*")
                    .hasRole("ADMIN")

                    // =========================
                    // INCOME TYPE
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/income_types", "/companies/*/income_types/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/income_types")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/income_types/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/travel_expenses",
                        "/companies/*/job/*/user/*/travel_expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/travel_expenses")
                    .authenticated()
                    // DELETE /travel_expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/travel_expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // PURCHASE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/purchases",
                        "/companies/*/job/*/user/*/purchases/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/purchases")
                    .authenticated()
                    .requestMatchers(POST, "/companies/*/job/*/user/*/purchase_operations")
                    .authenticated()
                    // DELETE /purchases - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/purchases/*")
                    .hasRole("ADMIN")

                    // =========================
                    // BANK FEE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/bank_fees",
                        "/companies/*/job/*/user/*/bank_fees/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/bank_fees")
                    .authenticated()
                    // DELETE /bank_fees - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/bank_fees/*")
                    .hasRole("ADMIN")

                    // =========================
                    // OTHER EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/other_expenses",
                        "/companies/*/job/*/user/*/other_expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/other_expenses")
                    .authenticated()
                    // DELETE /other_expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/other_expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EMPLOYEE PAYMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/employee_payments",
                        "/companies/*/job/*/user/*/employee_payments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/employee_payments")
                    .authenticated()
                    // DELETE /employee_payments - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/employee_payments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL PEOPLE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/travel_expenses/*/travel_people",
                        "/companies/*/job/*/user/*/travel_expenses/*/travel_people/*")
                    .authenticated()
                    .requestMatchers(
                        PUT, "/companies/*/job/*/user/*/travel_expenses/*/travel_people")
                    .authenticated()
                    // DELETE /travel_people - ADMIN uniquement
                    .requestMatchers(
                        DELETE, "/companies/*/job/*/user/*/travel_expenses/*/travel_people/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL MATERIALS
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/travel_expenses/*/travel_materials",
                        "/companies/*/job/*/user/*/travel_expenses/*/travel_materials/*")
                    .authenticated()
                    .requestMatchers(
                        PUT, "/companies/*/job/*/user/*/travel_expenses/*/travel_materials")
                    .authenticated()
                    // DELETE /travel_materials - ADMIN uniquement
                    .requestMatchers(
                        DELETE, "/companies/*/job/*/user/*/travel_expenses/*/travel_materials/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL EQUIPMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/travel_expenses/*/travel_equipment",
                        "/companies/*/job/*/user/*/travel_expenses/*/travel_equipment/*")
                    .authenticated()
                    .requestMatchers(
                        PUT, "/companies/*/job/*/user/*/travel_expenses/*/travel_equipment")
                    .authenticated()
                    // DELETE /travel_equipment - ADMIN uniquement
                    .requestMatchers(
                        DELETE, "/companies/*/job/*/user/*/travel_expenses/*/travel_equipment/*")
                    .hasRole("ADMIN")
                    .requestMatchers("/histories")
                    .authenticated()

                    // =========================
                    // OPERATION
                    // =========================
                    .requestMatchers(POST, "/companies/*/job/*/user/*/travel_operations")
                    .authenticated()

                    // =========================
                    // YEARLY REPORT
                    // =========================
                    .requestMatchers(GET, "/companies/*/yearly-report")
                    .authenticated()

                    // =========================
                    // LOAN
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/job/*/user/*/loans", "/companies/*/job/*/user/*/loans/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/loans")
                    .authenticated()
                    // DELETE /loans - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/loans/*")
                    .hasRole("ADMIN")

                    // =========================
                    // LOAN REPAYMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/job/*/user/*/loans/*/repayments",
                        "/companies/*/job/*/user/*/loans/*/repayments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/job/*/user/*/loans/*/repayments")
                    .authenticated()
                    // DELETE /loan_repayments - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/job/*/user/*/loans/*/repayments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // DEFAULT - Toute autre requête non auizer
                    // =========================
                    .anyRequest()
                    .denyAll());

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
