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
    public Long id;
    @NotEmpty(message = "Fullname is Required")
    public String fullName;
    @NotEmpty(message = "Username is Required")
    public String username;
    @NotEmpty(message = "Email is Required")
    @Email
    public String email;
    @NotEmpty(message = "Password is Required")
    public String password;
    @NotEmpty(message = "Role is Required")
    public String role;
}
