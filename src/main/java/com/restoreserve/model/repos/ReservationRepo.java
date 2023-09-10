package com.restoreserve.model.repos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Reservation;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
    // derived query JPA
    List<Reservation> findAllByUserId(Long userId);

    List<Reservation> findAllByRestaurantId(Long restaurantId);

    List<Reservation> findAllByStatusReservation(String statusReservation);

    boolean existsByReservationDate(LocalDateTime reservationDate);

    boolean existsByUserId(Long userId);

    boolean existsByRestaurantId(Long restaurantId);

    void deleteAllByUserId(Long userId);

    void deleteAllByRestaurantId(Long restaurantId);
}
