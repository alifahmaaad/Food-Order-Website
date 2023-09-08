package com.restoreserve.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuDto {
    @NotNull(message = "IdRestaurant is required")
    private Long restaurant;
    @NotEmpty(message = "name is required")
    private String name;
    @NotNull(message = "price is required")
    private Long price;
    private String description;
    private MultipartFile photo;
}
