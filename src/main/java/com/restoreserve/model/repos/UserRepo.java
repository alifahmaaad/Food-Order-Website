package com.restoreserve.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.User;

public interface UserRepo extends JpaRepository<User, Long>{
    
}
