package com.restoreserve.model.repos;


import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Restaurant;

public interface RestaurantRepo extends JpaRepository<Restaurant, Long>{
    boolean existsByName(String name);
    Restaurant findByUserOwnerId(Long ownerId);
}
