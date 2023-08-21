package com.restoreserve.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuDto {
    @NotEmpty(message = "id is Required")
    private Long id;
    @NotEmpty(message = "IdRestaurant is required")
    private Long idRestaurant;
    @NotEmpty(message = "name is required")
    private String name;
    @NotEmpty(message = "price is required")
    private Long price;
    private String Description;
}
