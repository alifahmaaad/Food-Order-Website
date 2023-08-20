package com.restoreserve.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restoreserve.model.entities.Restaurant;
import com.restoreserve.model.repos.RestaurantRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RestaurantService {
    @Autowired
    private RestaurantRepo restaurantRepo;
    public Restaurant create(Restaurant restaurant){
        return restaurantRepo.save(restaurant);
    }
    public List<Restaurant> getAllRestaurant(){
        return restaurantRepo.findAll();
    }
    public Restaurant getRestaurantById(Long id){
        return restaurantRepo.findById(id).get();
    }
    public Restaurant update(Restaurant restaurant){
        return restaurantRepo.save(restaurant);
    }
    public void deleteById(Long id){
        restaurantRepo.deleteById(id);
    }
}
