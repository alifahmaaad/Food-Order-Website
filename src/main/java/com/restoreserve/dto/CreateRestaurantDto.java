package com.restoreserve.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantDto {
    @NotEmpty(message = "Restaurant name required")
    private String name;
    @NotEmpty(message = "Id User with restaurant role required")
    private Long Owner;
}
