package com.restoreserve.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Reservation;

public interface ReservationRepo extends JpaRepository<Reservation, Long>{
    
}
