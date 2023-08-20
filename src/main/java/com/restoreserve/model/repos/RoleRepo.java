package com.restoreserve.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{
    
}
