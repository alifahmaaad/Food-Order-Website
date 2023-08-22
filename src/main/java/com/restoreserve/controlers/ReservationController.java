package com.restoreserve.controlers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.restoreserve.enums.RoleEnum;
import com.restoreserve.model.entities.Reservation;
import com.restoreserve.model.entities.User;
import com.restoreserve.security.ImplementUserDetails.CustomUserDetails;
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
    //customer only
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
    //Admin restaurant, only when auth iduser same with iduser(id_resto) in this reservation
    @GetMapping("/restaurant/{id}")
    public ResponseEntity<ResponseData<Reservation>> getReservationByIdRestaurant(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        
        if(isUserAllowedToAccessThisEndpoint(userDetails)||id.equals(userDetails.getId())){ 
            //need to check cause only user it self can update except his role superadmin or appadmin
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
        dataResponse.getMessage().add("You are not authorized to access data reservation on this restaurant");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }
    //Customer, only when auth iduser same with iduser(id_customer) in this reservation
    @GetMapping("/customer/{id}")
    public ResponseEntity<ResponseData<Reservation>> getReservationByIdUser(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if(isUserAllowedToAccessThisEndpoint(userDetails)||id.equals(userDetails.getId())){ 
            //need to check cause only user it self can update except his role superadmin or appadmin
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
        dataResponse.getMessage().add("You are not authorized to access data reservation");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }
    //customer, only when auth iduser same with iduser(id_customer) in this reservation
    @PutMapping("/customer/update")
    public ResponseEntity<ResponseData<Reservation>> update(@Valid @RequestBody UpdateReservationDto reservationDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if(isUserAllowedToAccessThisEndpoint(userDetails)||reservationDto.getUser().equals(userDetails.getId())){ 
            //need to check cause only user it self can update except his role superadmin or appadmin
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
        dataResponse.getMessage().add("You are not authorized to update data reservation");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }
    //restoadmin, only when auth iduser same with iduser(id_resto) in this reservation
    @PutMapping("/restaurant/approve/{id}")
    ResponseEntity<ResponseData<Reservation>> approve(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){                   
                Reservation reservation = reservationService.getReservationById(id);
                if(isUserAllowedToAccessThisEndpoint(userDetails)||reservation.getRestaurant().getId().equals(userDetails.getId())){ 
                    //need to check cause only user it self can update except his role superadmin or appadmin
                    reservation.setStatusReservation(ReservationEnum.Approve);
                    dataResponse.setPayload(reservationService.update(reservation));
                    dataResponse.setStatus(true);
                    dataResponse.getMessage().add("Success approve reservation");
                    return ResponseEntity.ok(dataResponse);
                } 
                dataResponse.getMessage().add("You are not authorized to update data reservation");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
                }
                dataResponse.getMessage().add("Reservation Id Not Found");
                return ResponseEntity.badRequest().body(dataResponse);
            } catch (Exception e) {
                dataResponse.getMessage().add(e.getMessage());
                return ResponseEntity.badRequest().body(dataResponse);
            }
        
    }
    //restoadmin, only when auth iduser same with iduser(id_resto) in this reservation
    @PutMapping("/restaurant/decline/{id}")
    public ResponseEntity<ResponseData<Reservation>> decline(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){
                Reservation reservation = reservationService.getReservationById(id);
                if(isUserAllowedToAccessThisEndpoint(userDetails)||reservation.getRestaurant().getId().equals(userDetails.getId())){ 
                    //need to check cause only user it self can update except his role superadmin or appadmin
                    reservation.setStatusReservation(ReservationEnum.Decline);
                    dataResponse.setPayload(reservationService.update(reservation));
                    dataResponse.setStatus(true);
                    dataResponse.getMessage().add("Success approve reservation");
                    return ResponseEntity.ok(dataResponse);
                } 
                dataResponse.getMessage().add("You are not authorized to update data reservation");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
            }
            dataResponse.getMessage().add("Reservation Id Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    //only when iduser same with auth iduser, either customer or restoadmin role
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseData<Reservation>> delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        ResponseData<Reservation> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(reservationService.isExistsByid(id)){
                Reservation reservation = reservationService.getReservationById(id);
                if(isUserAllowedToAccessThisEndpoint(userDetails)||reservation.getRestaurant().getId().equals(userDetails.getId())){ 
                    //need to check cause only user it self can update except his role superadmin or appadmin
                    reservationService.deleteById(id);;
                    dataResponse.setStatus(true);
                    dataResponse.getMessage().add("Success delete reservation");
                    return ResponseEntity.ok(dataResponse);
                } 
                dataResponse.getMessage().add("You are not authorized to update data reservation");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
            }
            dataResponse.getMessage().add("Reservation Id Not Found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }

    private boolean isUserAllowedToAccessThisEndpoint(CustomUserDetails userDetails) {
        User userData = userService.findByUsername(userDetails.getUsername());
        return userData.getRole().equals(RoleEnum.Super_Admin)||userData.getRole().equals(RoleEnum.App_Admin);
    }
}
