package ptsd14.find.doctor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ptsd14.find.doctor.security.JwtAuthenticationFilter;
import ptsd14.find.doctor.service.CustomUserDetailsService;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                corsConfig.setAllowedOrigins(java.util.List.of("http://localhost:3000"));
                corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                corsConfig.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "Accept"));
                corsConfig.setAllowCredentials(true);
                return corsConfig;
            }))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/api/webhook").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/api/payments/unpaid-appointments/**").permitAll()
                .requestMatchers("/api/payments/create-payment-intent/**").permitAll()
                .requestMatchers("/api/appointment-types/public/**", "/api/doctors/**", "/api/specializations/**", "/api/feedbacks/**").permitAll()
                .requestMatchers("/api/payments/pay-cash/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/appointments/doctor").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/confirm").hasRole("DOCTOR")
                .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel").hasAnyRole("DOCTOR", "PATIENT")
                .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/complete").hasRole("DOCTOR")
                .requestMatchers("/api/appointments/doctor", "/api/doctor/dashboard").hasRole("DOCTOR")
                .requestMatchers("/api/appointments/patient").hasRole("PATIENT")
                .requestMatchers("/api/patients/**","/api/appointments/**", "/api/appointment-types/**").hasAnyRole("PATIENT", "ADMIN")
                .requestMatchers("/api/hospitals/**","/api/users/**", "/api/payments/**","/api/dashboards/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .userDetailsService(userDetailsService)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .accessDeniedHandler(customAccessDeniedHandler())
            );
        return http.build();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"error\": \"You do not have permission to perform this action.\"}"
            );
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

