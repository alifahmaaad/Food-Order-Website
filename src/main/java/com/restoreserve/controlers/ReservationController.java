package com.restoreserve.controlers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.CreateReservationDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateReservationDto;
import com.restoreserve.enums.ReservationEnum;
import com.restoreserve.model.entities.Reservation;
import com.restoreserve.services.ReservationService;
import com.restoreserve.services.RestaurantService;
import com.restoreserve.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    //alluser customer only
    @PostMapping("/customer/create")
    public ResponseEntity<ResponseData<Reservation>> create(@Valid @RequestBody CreateReservationDto reservationDto){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByReservationDate(reservationDto.getReservationDate())){
                dataResponse.getMessage().add("Reservation on that datetime already booked");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            Reservation reservation = modelMapper.map(reservationDto, Reservation.class);
            reservation.setUser(userService.getUserById(reservationDto.getUser()));
            reservation.setRestaurant(restaurantService.getRestaurantById(reservationDto.getRestaurant()));
            reservation.setStatusReservation(ReservationEnum.Pending);
            dataResponse.setPayload(reservationService.create(reservation));
            dataResponse.setStatus(true);
            dataResponse.getMessage().add("Success make reservation, wait for resto to validation");
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //appadmin
    @GetMapping("/appadmin")
    public ResponseEntity<ResponseData<List<Reservation>>> getAllReservation(){
        ResponseData<List<Reservation>> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            dataResponse.setPayload(reservationService.getAllReservation());
            dataResponse.setStatus(true);
            dataResponse.getMessage().add("Success get all data reservation");
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //all
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Reservation>> getReservationById(@PathVariable Long id){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){
                dataResponse.setPayload(reservationService.getReservationById(id));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success get data reservation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("No reservation data with that id");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //Admin restaurant
    @GetMapping("/restaurant/{id}")
    public ResponseEntity<ResponseData<Reservation>> getReservationByIdRestaurant(@PathVariable Long id){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(restaurantService.isRestaurantExists(id)){
                dataResponse.setPayload(reservationService.getReservationById(id));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success get all data reservation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("No reservation data with that Restaurant id");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //Customer
    @GetMapping("/user/{id}")
    public ResponseEntity<ResponseData<Reservation>> getReservationByIdUser(@PathVariable Long id){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(userService.isUserExists(id)){
                dataResponse.setPayload(reservationService.getReservationById(id));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success get all data reservation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("No reservation data with that User id");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //all
    @PutMapping("/update")
    public ResponseEntity<ResponseData<Reservation>> update(@Valid @RequestBody UpdateReservationDto reservationDto){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(reservationDto.getId())){

                Reservation reservation = modelMapper.map(reservationDto, Reservation.class);
                reservation.setUser(userService.getUserById(reservationDto.getUser()));
                reservation.setRestaurant(restaurantService.getRestaurantById(reservationDto.getRestaurant()));
                reservation.setStatusReservation(ReservationEnum.Pending);
                dataResponse.setPayload(reservationService.update(reservation));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success update reservation, wait for resto to validation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Reservation Id Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //restoadmin
    @PutMapping("/restaurant/approve/{id}")
    ResponseEntity<ResponseData<Reservation>> approve(@PathVariable Long id){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){

                Reservation reservation = reservationService.getReservationById(id);
                reservation.setStatusReservation(ReservationEnum.Approve);
                dataResponse.setPayload(reservationService.update(reservation));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success approve reservation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Reservation Id Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //restoadmin
    @PutMapping("/restaurant/decline/{id}")
    public ResponseEntity<ResponseData<Reservation>> decline(@PathVariable Long id){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){

                Reservation reservation = reservationService.getReservationById(id);
                reservation.setStatusReservation(ReservationEnum.Decline);
                dataResponse.setPayload(reservationService.update(reservation));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success approve reservation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Reservation Id Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //all 
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Reservation>> delete(@PathVariable Long id){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){
               reservationService.deleteById(id);;
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success delete reservation");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Reservation Id Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
}
