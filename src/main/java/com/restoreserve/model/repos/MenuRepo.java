package com.restoreserve.model.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restoreserve.model.entities.Menu;

public interface MenuRepo extends JpaRepository<Menu, Long>{
    List<Menu> findAllByRestaurantId(final Long restaurantId);
}
