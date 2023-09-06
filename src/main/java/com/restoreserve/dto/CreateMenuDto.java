package com.restoreserve.dto;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMenuDto {
    @NotEmpty(message = "IdRestaurant is required")
    private Long restaurant;
    @NotEmpty(message = "name is required")
    private String name;
    @NotEmpty(message = "price is required")
    private Long price;
    private String description;
    private MultipartFile photo;
}
