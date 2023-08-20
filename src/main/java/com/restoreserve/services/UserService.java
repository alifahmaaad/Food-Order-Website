package com.restoreserve.services;

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
    public Iterable<User> getAllUser(){
        return userRepo.findAll();
    }
    public User update (User user){
        return userRepo.save(user);
    }
    public void deleteById(Long id){
        userRepo.deleteById(id);
    }
}
