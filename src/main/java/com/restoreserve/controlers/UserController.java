package com.restoreserve.controlers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.LoginRequest;
import com.restoreserve.dto.LoginResponse;
import com.restoreserve.dto.RegisterUserDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateUserDto;
import com.restoreserve.enums.RoleEnum;
import com.restoreserve.model.entities.Menu;
import com.restoreserve.model.entities.Restaurant;
import com.restoreserve.model.entities.User;
import com.restoreserve.security.ImplementUserDetails.CustomUserDetails;
import com.restoreserve.security.ImplementUserDetails.CustomUserDetailsService;
import com.restoreserve.services.MenuService;
import com.restoreserve.services.ReservationService;
import com.restoreserve.services.RestaurantService;
import com.restoreserve.services.UserService;
import com.restoreserve.utils.jwt.JwtUtil;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import jakarta.validation.Valid;

@RestController
@OpenAPIDefinition(info = @Info(title = "My App", description = "Some long and useful description", version = "v1", license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")))
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/login")
    public ResponseEntity<ResponseData<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        ResponseData<LoginResponse> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            dataResponse.getMessage().add(e.getMessage());
            dataResponse.getMessage().add("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        LoginResponse response = new LoginResponse(userService.findByUsername(userDetails.getUsername()), token);
        dataResponse.setStatus(true);
        dataResponse.setPayload(response);
        dataResponse.getMessage().add("Login Succesfully");
        return ResponseEntity.ok(dataResponse);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logout Successfully");
    }

    // Allrole
    @PostMapping("/register")
    public ResponseEntity<ResponseData<User>> register(@Valid @RequestBody RegisterUserDto userDto, Errors errs) {
        ResponseData<User> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if (errs.hasErrors()) {
            for (ObjectError err : errs.getAllErrors()) {
                dataResponse.getMessage().add(err.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(dataResponse);
        }
        User dataUser = modelMapper.map(userDto, User.class);
        String encodedPassword = passwordEncoder.encode(dataUser.getPassword());
        dataUser.setPassword(encodedPassword);
        try {
            if (userService.isUserExistsWithUsernameOrEmail(userDto.getUsername(), userDto.getEmail())) {
                dataResponse.getMessage().add("Username or Email already taken");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            dataResponse.setPayload(userService.create(dataUser));
            dataResponse.setStatus(true);
            dataResponse.getMessage().add("Your Account has been succesfully created or registered");
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }

    // UserSelfOnly
    // SuperAdmin and appadmin can access other user
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<User>> getUserById(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<User> dataResponse = new ResponseData<User>(false, new ArrayList<>(), null);
        if (isUserAllowedToAccessThisEndpoint(userDetails) || id.equals(userDetails.getId())) {
            // need to check cause only user it self can update except his role superadmin
            // or appadmin
            try {
                boolean isExists = userService.isUserExists(id);
                if (isExists) {
                    dataResponse.setPayload(userService.getUserById(id));
                    dataResponse.getMessage().add("Success get data user with id :" + id);
                    dataResponse.setStatus(true);
                    return ResponseEntity.ok(dataResponse);
                }
                dataResponse.getMessage().add("Failed: Data user with id :" + id + " Not Found");
                return ResponseEntity.badRequest().body(dataResponse);
            } catch (Exception e) {
                dataResponse.getMessage().add(e.getMessage());
                return ResponseEntity.badRequest().body(dataResponse);
            }
        }
        dataResponse.getMessage().add("You are not authorized to Update this User");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }

    // "/appadmin" for role app admin and super admin only
    @GetMapping("/appadmin")
    public ResponseEntity<ResponseData<List<User>>> getAllUser() {
        ResponseData<List<User>> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            dataResponse.setPayload(userService.getAllUser());
            dataResponse.getMessage().add("Success get All data user");
            dataResponse.setStatus(true);
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }

    }

    // UserselfOnly
    // SuperAdmin and appadmin can access other user
    @PutMapping("/update")
    public ResponseEntity<ResponseData<User>> updateUser(@RequestBody UpdateUserDto userDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<User> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        if (isUserAllowedToAccessThisEndpoint(userDetails) || userDto.getId().equals(userDetails.getId())) {
            // need to check cause only user it self can update except his role superadmin
            // or appadmin
            try {
                boolean isExists = userService.isUserExists(userDto.getId());
                if (isExists) {
                    User oldDataUser = userService.getUserById(userDto.getId());
                    User user = modelMapper.map(userDto, User.class);
                    if (user.getPassword() == null) {
                        user.setPassword(oldDataUser.getPassword());
                    } else {
                        String encodedPassword = passwordEncoder.encode(user.getPassword());
                        user.setPassword(encodedPassword);
                    }
                    if (user.getRole() == null) {
                        user.setRole(oldDataUser.getRole());
                    } else {
                        if (user.getRole() != oldDataUser.getRole()) {
                            if (reservationService.isExistsByUserid(userDto.getId())) {
                                reservationService.deleteByIdCutomer(userDto.getId());
                            }
                            if (restaurantService.isRestaurantExistsByOwner(userDto.getId())) {
                                Restaurant restaurant = restaurantService.getRestaurantByOwner(userDto.getId());
                                reservationService.deleteAllByRestaurantId(restaurant.getId());

                                List<Menu> dataMenu = menuService.getMenuByRestaurantId(restaurant.getId());
                                if (!dataMenu.isEmpty() || dataMenu != null) {
                                    for (Menu menu : dataMenu) {
                                        menuService.delete(menu.getId());
                                    }
                                }
                                restaurantService.deleteById(restaurant.getId());
                            }
                        }
                    }
                    dataResponse.setPayload(userService.update(user));
                    dataResponse.setStatus(true);
                    dataResponse.getMessage().add("Success Update user with id: " + user.getId());
                    return ResponseEntity.ok(dataResponse);
                }
                dataResponse.getMessage().add("User Not Exists id: " + userDto.getId());
                return ResponseEntity.badRequest().body(dataResponse);
            } catch (Exception e) {
                dataResponse.getMessage().add(e.getMessage());
                return ResponseEntity.badRequest().body(dataResponse);
            }
        }
        dataResponse.getMessage().add("You are not authorized to Update this User");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(dataResponse);
    }

    // "/appadmin" for role app admin and super admin only
    @DeleteMapping("/appadmin/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<?> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            boolean isExists = userService.isUserExists(id);
            if (isExists) {
                if (reservationService.isExistsByUserid(id)) {
                    reservationService.deleteByIdCutomer(id);
                }
                if (restaurantService.isRestaurantExistsByOwner(id)) {
                    Restaurant restaurant = restaurantService.getRestaurantByOwner(id);
                    reservationService.deleteAllByRestaurantId(restaurant.getId());

                    List<Menu> dataMenu = menuService.getMenuByRestaurantId(restaurant.getId());
                    if (!dataMenu.isEmpty() || dataMenu != null) {
                        for (Menu menu : dataMenu) {
                            menuService.delete(menu.getId());
                        }
                    }
                    restaurantService.deleteById(restaurant.getId());
                }
                if (!reservationService.isExistsByUserid(id) && !restaurantService.isRestaurantExistsByOwner(id)) {

                    userService.deleteById(id);
                    dataResponse.setStatus(true);
                    dataResponse.getMessage().add("User Deleted With id:" + id);
                    return ResponseEntity.ok(dataResponse);
                } else {
                    dataResponse.getMessage().add("Failed: User Not Found, id:" + id);
                    return ResponseEntity.badRequest().body(dataResponse);
                }
            }
            dataResponse.getMessage().add("Failed: User Not Found, id:" + id);
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }

    private boolean isUserAllowedToAccessThisEndpoint(CustomUserDetails userDetails) {
        User userData = userService.findByUsername(userDetails.getUsername());
        return userData.getRole().equals(RoleEnum.Super_Admin) || userData.getRole().equals(RoleEnum.App_Admin);
    }

}
