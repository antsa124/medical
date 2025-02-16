package com.antsasdomain.medicalapp.config;

import com.antsasdomain.medicalapp.filter.JwtAuthenticationFilter;
import com.antsasdomain.medicalapp.model.AdminLevel;
import com.antsasdomain.medicalapp.repository.AdminRepository;
import com.antsasdomain.medicalapp.service.PersonDetailsService;
import com.antsasdomain.medicalapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final PersonDetailsService personDetailsService;
    private final JwtUtils jwtUtils;
    private final AdminRepository adminRepository;

    public SecurityConfig(PersonDetailsService personDetailsService, JwtUtils jwtUtils, AdminRepository adminRepository) {
        this.personDetailsService = personDetailsService;
        this.jwtUtils = jwtUtils;
        this.adminRepository = adminRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AdminRepository adminRepository) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API usage
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No session storage
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Allow login & registration

                        // 🔹 FIXED: Prevent multiple SUPER_ADMIN registrations
                        .requestMatchers(HttpMethod.POST, "/api/admin/register")
                        .access((authenticationSupplier, context) -> {
                            boolean isSuperAdminExists = adminRepository.existsByAdminLevel(AdminLevel.SUPER_ADMIN);
                            var authentication = authenticationSupplier.get(); // Get the authenticated user

                            if (!isSuperAdminExists) {
                                return new AuthorizationDecision(true); // Allow first SUPER_ADMIN registration
                            }

                            // If a SUPER_ADMIN already exists, only they can create new admins
                            boolean isSuperAdmin = authentication != null && authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN"));

                            return new AuthorizationDecision(isSuperAdmin);
                        })

                        .requestMatchers(HttpMethod.PUT, "/api/admin/approve/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_MODERATOR") // Only these roles can approve users
                        .requestMatchers("/api/admin/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_MODERATOR") // Admin routes for these roles
                        .requestMatchers("/api/prescriptions/**")
                        .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_MODERATOR") // Only SUPER_ADMIN & MODERATOR
                        .requestMatchers("/api/doctors/**").hasAuthority("ROLE_DOCTOR")
                        .requestMatchers("/api/pharmacists/**").hasAuthority("ROLE_PHARMACIST")
                        .requestMatchers("/api/patients/**").hasAuthority("ROLE_PATIENT")

                        .anyRequest().authenticated() // Everything else requires authentication
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils, personDetailsService),
                        UsernamePasswordAuthenticationFilter.class); // Register JWT filter

        return http.build();
    }




    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(personDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
