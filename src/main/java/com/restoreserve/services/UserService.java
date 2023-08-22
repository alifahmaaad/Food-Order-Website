package com.restoreserve.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restoreserve.model.entities.User;
import com.restoreserve.model.repos.UserRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public User create(User user){
        return userRepo.save(user);
    }
    public User getUserById(Long id){
        return userRepo.findById(id).get();
    }
    public boolean isUserExistsWithUsernameOrEmail(String username,String email){
        return userRepo.existsByUsernameOrEmail(username,email);
    }
    public User findByUsername(String username){
        return userRepo.findByUsername(username);
    }
    public List<User> getAllUser(){
        return userRepo.findAll();
    }
    public boolean isUserExists(Long id){
        return userRepo.existsById(id);
    }
    public User update (User user){
        return userRepo.save(user);
    }
    public void deleteById(Long id){
        userRepo.deleteById(id);
    }
}
