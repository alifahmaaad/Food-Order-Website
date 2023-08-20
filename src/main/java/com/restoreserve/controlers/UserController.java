package com.restoreserve.controlers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.RegisterUserDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.model.entities.User;
import com.restoreserve.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<ResponseData<User>> register(@Valid @RequestBody RegisterUserDto userDto,Errors errs){
        ResponseData<User> dataResponse = new ResponseData<>();
        dataResponse.setPayload(null);
        dataResponse.setStatus(false);
        if(errs.hasErrors()){
            for (ObjectError err : errs.getAllErrors()) {
            dataResponse.getMessage().add(err.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(dataResponse);
        }
        User dataUser = modelMapper.map(userDto, User.class);
        try {
            dataResponse.setPayload(userService.create(dataUser));
            dataResponse.getMessage().add("Your Account has been succesfully created or registered");
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
}
