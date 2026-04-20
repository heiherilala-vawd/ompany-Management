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
                    .requestMatchers(PUT, "/companies/*/jobs")
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
                    // EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses",
                        "/companies/*/jobs/*/user/*/expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/expenses")
                    .authenticated()
                    // DELETE /expenses - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/jobs/*/user/*/expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // INCOME
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/incomes",
                        "/companies/*/jobs/*/user/*/incomes/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/incomes")
                    .authenticated()
                    // DELETE /incomes - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/jobs/*/user/*/incomes/*")
                    .hasAnyRole("ADMIN", "WAREHOUSE_WORKER", "EMPLOYEE", "ADMINISTRATION")

                    // =========================
                    // TRAVEL EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses",
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/expenses/*/travel_expenses")
                    .authenticated()
                    // DELETE /travel_expenses - ADMIN uniquement
                    .requestMatchers(
                        DELETE, "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // PURCHASE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/purchases",
                        "/companies/*/jobs/*/user/*/expenses/*/purchases/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/expenses/*/purchases")
                    .authenticated()
                    // DELETE /purchases - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/jobs/*/user/*/expenses/*/purchases/*")
                    .hasRole("ADMIN")

                    // =========================
                    // BANK FEE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/bank_fees",
                        "/companies/*/jobs/*/user/*/expenses/*/bank_fees/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/expenses/*/bank_fees")
                    .authenticated()
                    // DELETE /bank_fees - ADMIN uniquement
                    .requestMatchers(DELETE, "/companies/*/jobs/*/user/*/expenses/*/bank_fees/*")
                    .hasRole("ADMIN")

                    // =========================
                    // OTHER EXPENSE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/other_expenses",
                        "/companies/*/jobs/*/user/*/expenses/*/other_expenses/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/expenses/*/other_expenses")
                    .authenticated()
                    // DELETE /other_expenses - ADMIN uniquement
                    .requestMatchers(
                        DELETE, "/companies/*/jobs/*/user/*/expenses/*/other_expenses/*")
                    .hasRole("ADMIN")

                    // =========================
                    // EMPLOYEE PAYMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/employee_payments",
                        "/companies/*/jobs/*/user/*/expenses/*/employee_payments/*")
                    .authenticated()
                    .requestMatchers(PUT, "/companies/*/jobs/*/user/*/expenses/*/employee_payments")
                    .authenticated()
                    // DELETE /employee_payments - ADMIN uniquement
                    .requestMatchers(
                        DELETE, "/companies/*/jobs/*/user/*/expenses/*/employee_payments/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL PEOPLE
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_people",
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_people/*")
                    .authenticated()
                    .requestMatchers(
                        PUT,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_people")
                    .authenticated()
                    // DELETE /travel_people - ADMIN uniquement
                    .requestMatchers(
                        DELETE,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_people/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL MATERIALS
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_materials",
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_materials/*")
                    .authenticated()
                    .requestMatchers(
                        PUT,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_materials")
                    .authenticated()
                    // DELETE /travel_materials - ADMIN uniquement
                    .requestMatchers(
                        DELETE,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_materials/*")
                    .hasRole("ADMIN")

                    // =========================
                    // TRAVEL EQUIPMENT
                    // =========================
                    .requestMatchers(
                        GET,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_equipment",
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_equipment/*")
                    .authenticated()
                    .requestMatchers(
                        PUT,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_equipment")
                    .authenticated()
                    // DELETE /travel_equipment - ADMIN uniquement
                    .requestMatchers(
                        DELETE,
                        "/companies/*/jobs/*/user/*/expenses/*/travel_expenses/*/travel_equipment/*")
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
