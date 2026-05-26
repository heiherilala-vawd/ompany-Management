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
                    .requestMatchers(GET, "/companies/*/users", "/companies/*/users/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/users")
                    .authenticated()

                    // DELETE /users - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/users/*")
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
                    .requestMatchers(GET, "/companies/*/materials", "/companies/*/materials/*")
                    .authenticated()
                    // PUT /materials - ADMIN, ADMINISTRATION, WAREHOUSE_WORKER
                    .requestMatchers(PUT, "/companies/*/materials")
                    .authenticated()
                    // DELETE /materials - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/materials/*")
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
                    .requestMatchers(GET, "/companies/*/yearly_report")
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
                    // OTHER EXPENSE TYPE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/other_expense_types",
                        "/companies/*/other_expense_types/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/other_expense_types")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/other_expense_types/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MAINTENANCE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/equipment/*/maintenances",
                        "/companies/*/equipment/*/maintenances/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/equipment/*/maintenances")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/equipment/*/maintenances/*")
                    .hasRole("ADMIN")

                    // =========================
                    // COMPANY FIXED COST
                    // =========================
                    .requestMatchers(GET, "/companies/*/fixed_costs", "/companies/*/fixed_costs/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/fixed_costs")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/fixed_costs/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TASK
                    // =========================
                    .requestMatchers(GET, "/companies/*/tasks", "/companies/*/tasks/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/tasks")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/tasks/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TASK SCHEDULE
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/task_schedules", "/companies/*/task_schedules/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/task_schedules")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/task_schedules/*")
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
                    // HR - LEAVE TYPES
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/leave_types",
                        "/companies/*/leave_types/*",
                        "/companies/*/leave_configs",
                        "/companies/*/leave_configs/*",
                        "/companies/*/leaves",
                        "/companies/*/leaves/*",
                        "/companies/*/leave_balances",
                        "/companies/*/leave_balances/employees_without_leave")
                    .authenticated()
                    .requestMatchers(
                        PUT,
                        "/companies/*/leave_types",
                        "/companies/*/leave_configs",
                        "/companies/*/leaves")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/leaves/*")
                    .hasRole("ADMIN")

                    // =========================
                    // DEPARTMENT
                    // =========================
                    .requestMatchers(GET, "/companies/*/departments", "/companies/*/departments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/departments")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/departments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // CASH ACCOUNT
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/cash_accounts", "/companies/*/cash_accounts/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/cash_accounts")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/cash_accounts/*")
                    .hasRole("ADMIN")

                    // =========================
                    // CASH TRANSACTION
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/cash_accounts/*/transactions",
                        "/companies/*/cash_accounts/*/transactions/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/cash_accounts/*/transactions")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/cash_accounts/*/transactions/*")
                    .hasRole("ADMIN")

                    // =========================
                    // BUDGET LINE
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/budget_lines", "/companies/*/budget_lines/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/budget_lines")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/budget_lines/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EQUIPMENT USAGE
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/equipment_usage", "/companies/*/equipment_usage/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/equipment_usage")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/equipment_usage/*/return")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/equipment_usage/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MATERIAL CONSUMPTION
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/material_consumption",
                        "/companies/*/material_consumption/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/material_consumption")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/material_consumption/*/complete")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/material_consumption/*/return")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/material_consumption/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TEAM
                    // =========================
                    .requestMatchers(GET, "/companies/*/teams", "/companies/*/teams/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/teams")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/teams/*")
                    .hasRole("ADMIN")

                    // =========================
                    // SUPPLIER
                    // =========================
                    .requestMatchers(GET, "/companies/*/suppliers", "/companies/*/suppliers/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/suppliers")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/suppliers/*")
                    .hasRole("ADMIN")

                    // =========================
                    // PURCHASE ORDER
                    // =========================
                    .requestMatchers(
                        GET, "/companies/*/purchase_orders", "/companies/*/purchase_orders/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/purchase_orders")
                    .authenticated()
                    .requestMatchers(DELETE, "/companies/*/purchase_orders/*")
                    .hasRole("ADMIN")

                    // =========================
                    // DASHBOARD
                    // =========================
                    .requestMatchers(GET, "/companies/*/dashboard/**")
                    .authenticated()

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
