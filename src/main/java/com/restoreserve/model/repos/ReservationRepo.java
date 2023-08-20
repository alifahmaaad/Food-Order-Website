package com.restoreserve.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Reservation;

public interface ReservationRepo extends JpaRepository<Reservation, Long>{
    //derived query JPA
    List<Reservation> findbyIdUser(Long id_user);
    List<Reservation> findbyIdRestaurant(Long id_restaurant);   
}
