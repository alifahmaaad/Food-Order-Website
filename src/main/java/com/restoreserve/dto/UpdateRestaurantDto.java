package com.restoreserve.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRestaurantDto {
    @NotEmpty(message = "Restaurant id required")
    private Long id;
    @NotEmpty(message = "Restaurant name required")
    private String name;
    @NotEmpty(message = "Id User with restaurant role required")
    private Long owner;
    private String tags;
    private MultipartFile photo;
}
