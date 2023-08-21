package com.restoreserve.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReservationDto {
    private Date reservationDate;
    private Integer numberOfGuest;
    private String statusReservation;
    private Long user;
    private Long restaurant;
}
