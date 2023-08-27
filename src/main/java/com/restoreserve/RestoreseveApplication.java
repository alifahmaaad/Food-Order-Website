package com.restoreserve;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.restoreserve.utils.jwt.JwtUtil;

@SpringBootApplication
public class RestoreseveApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestoreseveApplication.class, args);
	}

    @Bean
    ModelMapper modelMapper(){
		return new ModelMapper();
	}
	@Bean
    JwtUtil jwtUtil() {
        return new JwtUtil();
    }
	@Bean
    WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*"); 
        };
    };
	}

}
