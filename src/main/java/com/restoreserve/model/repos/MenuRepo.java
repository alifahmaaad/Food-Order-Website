package com.restoreserve.model.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Menu;

public interface MenuRepo extends JpaRepository<Menu, Long>{
    
}
