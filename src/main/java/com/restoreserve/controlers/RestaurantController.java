package com.restoreserve.controlers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.CreateRestaurantDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateRestaurantDto;
import com.restoreserve.enums.RoleEnum;
import com.restoreserve.model.entities.Restaurant;
import com.restoreserve.model.entities.User;
import com.restoreserve.security.ImplementUserDetails.CustomUserDetails;
import com.restoreserve.services.ImageService;
import com.restoreserve.services.RestaurantService;
import com.restoreserve.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/restaurant")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ModelMapper modelMapper;

    // UserRole adminresto only with his resto only
    // appadmin and superadmin can access for all user and resto
    @PostMapping("/create")
    public ResponseEntity<ResponseData<Restaurant>> create(@Valid @ModelAttribute CreateRestaurantDto restaurantDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if (isUserAllowedToAccessThisEndpoint(userDetails) || restaurantDto.getOwner().equals(userDetails.getId())) {
            System.out.print(restaurantDto);
            try {
                boolean isExist = restaurantService.isRestaurantExistsByName(restaurantDto.getName());
                if (isExist) {
                    dataResponse.getMessage().add("Name of restaurant already taken");
                    return ResponseEntity.badRequest().body(dataResponse);
                }
                User dataUser = userService.getUserById(restaurantDto.getOwner());
                if (dataUser != null) {
                    Restaurant restaurant = modelMapper.map(restaurantDto, Restaurant.class);
                    restaurant.setUserOwner(dataUser);
                    if (!restaurantDto.getPhoto().isEmpty()) {
                        String imagePath = imageService.saveImage(restaurantDto.getPhoto(), "profile/resto");
                        restaurant.setPhoto(imagePath);
                    } else {
                        restaurant.setPhoto("uploads/profile/resto/default.png");
                    }

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
        dataResponse.getMessage().add("You are not authorized to Update this restaurant");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }

    // "/customer" for role customer and super admin only
    @GetMapping("/all")
    public ResponseEntity<ResponseData<List<Restaurant>>> getAllRestaurant() {
        ResponseData<List<Restaurant>> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
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

    // UserRole adminresto only with his resto only
    // appadmin and superadmin can access for all user and resto
    @GetMapping("/owner/{id}")
    public ResponseEntity<ResponseData<Restaurant>> getRestaurantByidOwner(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if (isUserAllowedToAccessThisEndpoint(userDetails) || id.equals(userDetails.getId())) {
            // need to check cause only user it self can update except his role superadmin
            // or appadmin
            try {
                if (restaurantService.getRestaurantByOwner(id) != null) {
                    dataResponse.setPayload(restaurantService.getRestaurantByOwner(id));
                    dataResponse.getMessage().add("success get data restaurant by Owner id:" + id);
                    dataResponse.setStatus(true);
                    return ResponseEntity.ok(dataResponse);
                }
                dataResponse.getMessage().add("Restaurant with that Ownerid not found");
                return ResponseEntity.badRequest().body(dataResponse);
            } catch (Exception e) {
                dataResponse.getMessage().add(e.getMessage());
                return ResponseEntity.badRequest().body(dataResponse);
            }
        }
        dataResponse.getMessage().add("You are not authorized to Get data this restaurant ");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Restaurant>> getRestaurantByid(@PathVariable Long id) {
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if (restaurantService.isRestaurantExists(id)) {
                dataResponse.setPayload(restaurantService.getRestaurantById(id));
                dataResponse.getMessage().add("success get data restaurant by  id:" + id);
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

    // UserRole adminresto only with his resto only
    // appadmin and superadmin can access for all user and resto
    @PutMapping("/update")
    public ResponseEntity<ResponseData<Restaurant>> updateRestaurant(
            @Valid @ModelAttribute UpdateRestaurantDto restaurantDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        System.out.println(restaurantDto);
        if (isUserAllowedToAccessThisEndpoint(userDetails) || restaurantDto.getOwner().equals(userDetails.getId())) {
            // need to check cause only user it self can update except his role superadmin
            // or appadmin
            try {
                if (restaurantService.isRestaurantExists(restaurantDto.getId())) {
                    Restaurant restaurantPrev = restaurantService.getRestaurantById(restaurantDto.getId());
                    Restaurant restaurant = modelMapper.map(restaurantDto, Restaurant.class);
                    User dataUser = userService.getUserById(restaurantDto.getOwner());
                    restaurant.setUserOwner(dataUser);
                    if (!restaurantDto.getPhoto().isEmpty()) {
                        imageService.deleteImage(restaurantPrev.getPhoto());
                        String imagePath = imageService.saveImage(restaurantDto.getPhoto(), "profile/resto");
                        restaurant.setPhoto(imagePath);
                    } else {
                        restaurant.setPhoto(restaurantPrev.getPhoto());
                    }
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
        dataResponse.getMessage().add("You are not authorized to Update this restaurant");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }

    // UserRole adminresto only with his resto only
    // appadmin and superadmin can access for all user and resto
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseData<Restaurant>> delete(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<Restaurant> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if (isUserAllowedToAccessThisEndpoint(userDetails) || id.equals(userDetails.getId())) {
            // need to check cause only user it self can update except his role superadmin
            // or appadmin
            try {
                if (restaurantService.isRestaurantExists(id)) {
                    restaurantService.deleteById(id);
                    dataResponse.getMessage().add("success delete data restaurant by id:" + id);
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
        dataResponse.getMessage().add("You are not authorized to delete this restaurant");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }

    private boolean isUserAllowedToAccessThisEndpoint(CustomUserDetails userDetails) {
        User userData = userService.findByUsername(userDetails.getUsername());
        return userData.getRole().equals(RoleEnum.Super_Admin) || userData.getRole().equals(RoleEnum.App_Admin);
    }
}
