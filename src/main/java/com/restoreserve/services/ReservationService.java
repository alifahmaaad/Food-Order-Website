package com.restoreserve.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restoreserve.model.entities.Reservation;
import com.restoreserve.model.repos.ReservationRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservationService {
    @Autowired
    private ReservationRepo reservationRepo;

    public Reservation create(Reservation reservation) {
        return reservationRepo.save(reservation);
    }

    public List<Reservation> getAllReservation() {
        return reservationRepo.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationRepo.findById(id).get();
    }

    public List<Reservation> getReservationByCustomerId(Long customer_id) {
        return reservationRepo.findAllByUserId(customer_id);
    }

    public List<Reservation> getReservationByRestaurantId(Long restaurant_id) {
        return reservationRepo.findAllByRestaurantId(restaurant_id);
    }

    public List<Reservation> getByReservationStatus(String status) {
        return reservationRepo.findAllByStatusReservation(status);
    }

    public boolean isExistsByReservationDate(LocalDateTime date) {
        return reservationRepo.existsByReservationDate(date);
    }

    public boolean isExistsByid(Long id) {
        return reservationRepo.existsById(id);
    }

    public Reservation update(Reservation reservation) {
        return reservationRepo.save(reservation);
    }

    public void deleteById(Long Id) {
        reservationRepo.deleteById(Id);
    }
}
