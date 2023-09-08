package com.restoreserve.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReservationDto {
    @NotNull(message = "Reservation Date is required")
    private LocalDateTime reservationDate;
    @NotNull(message = "Number of gusest is required")
    private Integer numberOfGuest;
    @NotNull(message = "id user is required")
    private Long user;
    @NotNull(message = "id restaurant is required")
    private Long restaurant;
}
