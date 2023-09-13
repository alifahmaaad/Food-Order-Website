package com.restoreserve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @NotEmpty(message = "Id is Required")
    private Long id;
    @NotEmpty(message = "Fullname is Required")
    private String fullName;
    @NotEmpty(message = "Username is Required")
    private String username;
    @NotEmpty(message = "Email is Required")
    @Email
    private String email;
    private String password;
    private String role;
}
