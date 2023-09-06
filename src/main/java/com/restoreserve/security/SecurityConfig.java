package com.restoreserve.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restoreserve.enums.RoleEnum;
import com.restoreserve.utils.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("authorization", "content-type",
                                "x-auth-token");
            };

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/resources/**")
                        .addResourceLocations("/resources/");
            }
        };
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/static/**", "/resources/**",
                                "/resources/static/**")
                        .permitAll()
                        .requestMatchers("/api/user/login", "/api/user/logout", "/api/user/register",
                                "/api/restaurant/{id}")
                        .permitAll()
                        .requestMatchers("/api/user/appadmin/**")
                        .hasAnyRole(RoleEnum.App_Admin.toString(), RoleEnum.Super_Admin.toString())
                        .requestMatchers("/api/restaurant/customer/**")
                        .hasAnyRole(RoleEnum.Customer.toString(), RoleEnum.App_Admin.toString(),
                                RoleEnum.Super_Admin.toString())
                        .requestMatchers("/api/restaurant/**")
                        .hasAnyRole(RoleEnum.Restaurant_Admin.toString(), RoleEnum.App_Admin.toString(),
                                RoleEnum.Super_Admin.toString())
                        .requestMatchers("/api/reservation/appadmin/**")
                        .hasAnyRole(RoleEnum.App_Admin.toString(), RoleEnum.Super_Admin.toString())
                        .requestMatchers("/api/reservation/customer/**")
                        .hasAnyRole(RoleEnum.Customer.toString(), RoleEnum.App_Admin.toString(),
                                RoleEnum.Super_Admin.toString())
                        .requestMatchers("/api/menu/restaurant/**")
                        .hasAnyRole(RoleEnum.Restaurant_Admin.toString(), RoleEnum.App_Admin.toString(),
                                RoleEnum.Super_Admin.toString())
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/api/auth/**")
                .requestMatchers("/v3/api-docs/**")
                .requestMatchers("configuration/**")
                .requestMatchers("/swagger*/**")
                .requestMatchers("/webjars/**")
                .requestMatchers("/swagger-ui/**")
                .requestMatchers("/secure/**")
                .requestMatchers("/uploads/**");

    }

}
