package com.restoreserve.dto;

import java.util.Date;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateReservationDto {
    @NotEmpty(message = "ID is required")
    private Long id;
    @NotEmpty(message = "Reservation Date is required")
    private Date reservationDate;
    @NotEmpty(message = "Number of gusest is required")
    private Integer numberOfGuest;
    @NotEmpty(message = "id user is required")
    private Long user;
    @NotEmpty(message = "id restaurant is required")
    private Long restaurant;
}
