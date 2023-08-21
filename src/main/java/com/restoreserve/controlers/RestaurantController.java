package com.restoreserve.controlers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.CreateRestaurantDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateRestaurantDto;
import com.restoreserve.model.entities.Restaurant;
import com.restoreserve.model.entities.User;
import com.restoreserve.services.RestaurantService;
import com.restoreserve.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @PostMapping("/create")
    public ResponseEntity<ResponseData<Restaurant>> create(@Valid @RequestBody CreateRestaurantDto restaurantDto){
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, null, null);
        try {
            boolean isExist = restaurantService.isRestaurantExistsByName(restaurantDto.getName());
            if(isExist){
                dataResponse.getMessage().add("Name of restaurant already taken");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            User dataUser = userService.getUserById(restaurantDto.getOwner());
            if(dataUser!=null){
                Restaurant restaurant = modelMapper.map(restaurantDto, Restaurant.class);
                restaurant.setUserOwner(dataUser);
                dataResponse.setPayload(restaurantService.create(restaurant));
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("Success create restaurant");
                return ResponseEntity.ok().body(dataResponse);
            }
            dataResponse.getMessage().add("User with Id owner not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    // "/customer" for role customer and super admin only
    @GetMapping("/costumer/all")
    public ResponseEntity<ResponseData<List<Restaurant>>> getAllRestaurant() {
        ResponseData<List<Restaurant>> dataResponse = new ResponseData<>(false, null, null);
        try {
            dataResponse.setPayload(restaurantService.getAllRestaurant());
            dataResponse.getMessage().add("success get all data restaurant");
            dataResponse.setStatus(true);
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Restaurant>> getRestaurantByid(@PathVariable Long id){
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, null, null);
        try {
            if(restaurantService.isRestaurantExists(id)){
                dataResponse.setPayload(restaurantService.getRestaurantById(id));
                dataResponse.getMessage().add("success get data restaurant by id:"+id);
                dataResponse.setStatus(true);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant with that id not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<ResponseData<Restaurant>> updateRestaurant(@Valid @RequestBody UpdateRestaurantDto restaurantDto){
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, null, null);
        try {
            if(restaurantService.isRestaurantExists(restaurantDto.getId())){
                Restaurant restaurant = modelMapper.map(restaurantDto, Restaurant.class);
                User dataUser = userService.getUserById(restaurantDto.getOwner());
                restaurant.setUserOwner(dataUser);
                dataResponse.setPayload(restaurantService.update(restaurant));
                dataResponse.getMessage().add("Restaurant has been updated");
                dataResponse.setStatus(true);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant with that id not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseData<Restaurant>> delete(@PathVariable Long id){
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, null, null);
        try {
            if(restaurantService.isRestaurantExists(id)){
                restaurantService.deleteById(id);
                dataResponse.getMessage().add("success delete data restaurant by id:"+id);
                dataResponse.setStatus(true);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant with that id not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
}
