package com.example.demo.endpoint.rest.security;

import static org.springframework.http.HttpMethod.*;

import com.example.demo.endpoint.rest.security.jwt.CompanyScopedFilter;
import com.example.demo.endpoint.rest.security.jwt.JwtAccessDeniedHandler;
import com.example.demo.endpoint.rest.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.endpoint.rest.security.jwt.JwtAuthenticationFilter;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
  private final CompanyScopedFilter companyScopedFilter;
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
                    .requestMatchers("/auth/whoami", "/auth/password")
                    .authenticated()

                    // =========================
                    // USERS
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/users", "/users/*/companies/*/users/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/users")
                    .authenticated()

                    // DELETE /users - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/users/*")
                    .hasRole("ADMIN")

                    // =========================
                    // COMPANY
                    // =========================
                    .requestMatchers(GET, "/users/*/companies/*")
                    .authenticated()
                    .requestMatchers(GET, "/companies")
                    .authenticated()
                    .requestMatchers(PUT, "/companies")
                    .authenticated()
                    // DELETE /companies - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*")
                    .hasRole("ADMIN")

                    // =========================
                    // JOB
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/jobs", "/users/*/companies/*/jobs/*")
                    .authenticated()
                    .requestMatchers(GET, "/users/*/companies/*/jobs/*/users")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/users")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/users")
                    .authenticated()
                    // DELETE /jobs - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*")
                    .hasRole("ADMIN")

                    // =========================
                    // WAREHOUSE
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/warehouses", "/users/*/companies/*/warehouses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/warehouses")
                    .authenticated()
                    // DELETE /warehouses - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/warehouses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EQUIPMENT
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/equipments", "/users/*/companies/*/equipments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/equipments")
                    .authenticated()
                    // DELETE /equipment - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/equipments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MATERIAL
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/materials", "/users/*/companies/*/materials/*")
                    .authenticated()
                    // PUT /materials - ADMIN, ADMINISTRATION, WAREHOUSE_WORKER
                    .requestMatchers(PUT, "/users/*/companies/*/materials")
                    .authenticated()
                    // DELETE /materials - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/materials/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MATERIAL WAREHOUSE
                    // =========================
                    .requestMatchers(GET, "/users/*/companies/*/material_warehouse")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/material_warehouse")
                    .authenticated()

                    // =========================
                    // EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/expenses",
                        "/users/*/companies/*/jobs/*/expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/expenses")
                    .authenticated()
                    // DELETE /expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // INCOME
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/incomes",
                        "/users/*/companies/*/jobs/*/incomes/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/incomes")
                    .authenticated()
                    // DELETE /incomes - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/incomes/*")
                    .hasAnyRole("ADMIN", "WAREHOUSE_WORKER", "EMPLOYEE", "ADMINISTRATION")

                    // =========================
                    // INCOME RECEIPT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/incomes_receipts",
                        "/users/*/companies/*/jobs/*/incomes_receipts/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/incomes_receipts")
                    .authenticated()
                    // DELETE /receipts - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/incomes_receipts/*")
                    .hasRole("ADMIN")

                    // =========================
                    // INCOME TYPE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/income_types",
                        "/users/*/companies/*/income_types/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/income_types")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/income_types/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/travel_expenses",
                        "/users/*/companies/*/jobs/*/travel_expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/travel_expenses")
                    .authenticated()
                    // DELETE /travel_expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/travel_expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // PURCHASE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/purchases",
                        "/users/*/companies/*/jobs/*/purchases/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/purchases")
                    .authenticated()
                    .requestMatchers(POST, "/users/*/companies/*/jobs/*/purchase_operations")
                    .authenticated()
                    // DELETE /purchases - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/purchases/*")
                    .hasRole("ADMIN")

                    // =========================
                    // BANK FEE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/bank_fees",
                        "/users/*/companies/*/jobs/*/bank_fees/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/bank_fees")
                    .authenticated()
                    // DELETE /bank_fees - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/bank_fees/*")
                    .hasRole("ADMIN")

                    // =========================
                    // OTHER EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/other_expenses",
                        "/users/*/companies/*/jobs/*/other_expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/other_expenses")
                    .authenticated()
                    // DELETE /other_expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/other_expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EMPLOYEE PAYMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/employee_payments",
                        "/users/*/companies/*/jobs/*/employee_payments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/employee_payments")
                    .authenticated()
                    // DELETE /employee_payments - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/employee_payments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL PEOPLE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/travel_people",
                        "/users/*/companies/*/jobs/*/travel_people/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/travel_people")
                    .authenticated()
                    // DELETE /travel_people - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/travel_people/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL MATERIALS
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/travel_materials",
                        "/users/*/companies/*/jobs/*/travel_materials/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/travel_materials")
                    .authenticated()
                    // DELETE /travel_materials - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/travel_materials/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL EQUIPMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/travel_equipments",
                        "/users/*/companies/*/jobs/*/travel_equipments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/travel_equipments")
                    .authenticated()
                    // DELETE /travel_equipment - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/travel_equipments/*")
                    .hasRole("ADMIN")
                    .requestMatchers("/histories")
                    .authenticated()

                    // =========================
                    // OPERATION
                    // =========================
                    .requestMatchers(POST, "/users/*/companies/*/jobs/*/travel_operations")
                    .authenticated()

                    // =========================
                    // YEARLY REPORT
                    // =========================
                    .requestMatchers(GET, "/users/*/companies/*/yearly_report")
                    .authenticated()

                    // =========================
                    // LOAN
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/loans",
                        "/users/*/companies/*/jobs/*/loans/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/loans")
                    .authenticated()
                    // DELETE /loans - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/loans/*")
                    .hasRole("ADMIN")

                    // =========================
                    // OTHER EXPENSE TYPE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/other_expense_types",
                        "/users/*/companies/*/other_expense_types/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/other_expense_types")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/other_expense_types/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MAINTENANCE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/maintenances",
                        "/users/*/companies/*/maintenances/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/maintenances")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/maintenances/*")
                    .hasRole("ADMIN")

                    // =========================
                    // COMPANY FIXED COST
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/fixed_costs",
                        "/users/*/companies/*/fixed_costs/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/fixed_costs")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/fixed_costs/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TASK
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/tasks", "/users/*/companies/*/tasks/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/tasks")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/tasks/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TASK SCHEDULE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/task_schedules",
                        "/users/*/companies/*/task_schedules/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/task_schedules")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/task_schedules/*")
                    .hasRole("ADMIN")

                    // =========================
                    // LOAN REPAYMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/jobs/*/loan_repayments",
                        "/users/*/companies/*/jobs/*/loan_repayments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/jobs/*/loan_repayments")
                    .authenticated()
                    // DELETE /loan_repayments - ADMIN uniquement
                    .requestMatchers(DELETE, "/users/*/companies/*/jobs/*/loan_repayments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // HR - LEAVE TYPES
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/leave_types",
                        "/users/*/companies/*/leave_types/*",
                        "/users/*/companies/*/leave_configs",
                        "/users/*/companies/*/leave_configs/*",
                        "/users/*/companies/*/leaves",
                        "/users/*/companies/*/leaves/*",
                        "/users/*/companies/*/leave_balances",
                        "/users/*/companies/*/leave_balances/employees_without_leave")
                    .authenticated()
                    .requestMatchers(
                        PUT,
                        "/users/*/companies/*/leave_types",
                        "/users/*/companies/*/leave_configs",
                        "/users/*/companies/*/leaves")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/leaves/*")
                    .hasRole("ADMIN")

                    // =========================
                    // DEPARTMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/departments",
                        "/users/*/companies/*/departments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/departments")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/departments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // CASH ACCOUNT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/cash_accounts",
                        "/users/*/companies/*/cash_accounts/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/cash_accounts")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/cash_accounts/*")
                    .hasRole("ADMIN")

                    // =========================
                    // CASH TRANSACTION
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/cash_accounts/*/transactions",
                        "/users/*/companies/*/cash_accounts/*/transactions/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/cash_accounts/*/transactions")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/cash_accounts/*/transactions/*")
                    .hasRole("ADMIN")

                    // =========================
                    // BUDGET LINE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/budget_lines",
                        "/users/*/companies/*/budget_lines/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/budget_lines")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/budget_lines/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EQUIPMENT USAGE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/equipment_usages",
                        "/users/*/companies/*/equipment_usages/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/equipment_usages")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/equipment_usages/*/return")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/equipment_usages/*")
                    .hasRole("ADMIN")

                    // =========================
                    // MATERIAL CONSUMPTION
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/material_consumptions",
                        "/users/*/companies/*/material_consumptions/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/material_consumptions")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/material_consumptions/*/complete")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/material_consumptions/*/return")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/material_consumptions/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TEAM
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/teams", "/users/*/companies/*/teams/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/teams")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/teams/*")
                    .hasRole("ADMIN")

                    // =========================
                    // SUPPLIER
                    // =========================
                    .requestMatchers(
                        GET, "/users/*/companies/*/suppliers", "/users/*/companies/*/suppliers/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/suppliers")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/suppliers/*")
                    .hasRole("ADMIN")

                    // =========================
                    // ORGANIZATION
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/organizations",
                        "/users/*/companies/*/organizations/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/organizations")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/organizations/*")
                    .hasRole("ADMIN")

                    // =========================
                    // PURCHASE ORDER
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/purchase_orders",
                        "/users/*/companies/*/purchase_orders/*")
                    .authenticated()
                    .requestMatchers(PUT, "/users/*/companies/*/purchase_orders")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/purchase_orders/*")
                    .hasRole("ADMIN")

                    // =========================
                    // DASHBOARD
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/dashboard/*",
                        "/users/*/companies/*/dashboard/*/*",
                        "/users/*/companies/*/dashboard/*/*/*")
                    .authenticated()

                    // =========================
                    // NOTIFICATIONS
                    // =========================
                    .requestMatchers(
                        GET,
                        "/users/*/companies/*/notifications",
                        "/users/*/companies/*/notifications/unread_count",
                        "/users/*/companies/*/notifications/*")
                    .authenticated()
                    .requestMatchers(
                        PUT,
                        "/users/*/companies/*/notifications",
                        "/users/*/companies/*/notifications/*/read",
                        "/users/*/companies/*/notifications/*/complete")
                    .authenticated()
                    .requestMatchers(DELETE, "/users/*/companies/*/notifications/*")
                    .hasRole("ADMIN")

                    // =========================
                    // INCOMES EXCEL
                    // =========================
                    .requestMatchers(GET, "/users/*/companies/*/jobs/*/incomes/excel")
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
  public FilterRegistrationBean<CompanyScopedFilter> companyScopedFilterRegistration(
      CompanyScopedFilter filter) {
    FilterRegistrationBean<CompanyScopedFilter> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
