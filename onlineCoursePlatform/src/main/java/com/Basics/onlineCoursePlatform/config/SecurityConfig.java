package com.Basics.onlineCoursePlatform.config;

import com.Basics.onlineCoursePlatform.exception.CustomAccessDeniedHandler;
import com.Basics.onlineCoursePlatform.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedOrigins("*")
                        .allowedHeaders("*");
            }
        };
    }
    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/forgot-password", "/api/auth/reset-password", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html","/webjars/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/courses").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT,"/api/courses/**").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/courses/**").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET,"api/courses/my-courses").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET,"api/courses/enrolled").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST,"api/courses/{id}/enroll").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST,"/api/courses/{courseId}/sections").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.PUT,"/api/courses/{courseId}/sections").hasRole("INSTRUCTOR")
                        .requestMatchers(HttpMethod.DELETE,"/api/courses/{courseId}/sections").hasRole("INSTRUCTOR").anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedHandler(customAccessDeniedHandler())
        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}



