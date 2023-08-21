package com.restoreserve.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.restoreserve.model.entities.Menu;
import com.restoreserve.model.repos.MenuRepo;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MenuService {
    @Autowired
    private MenuRepo menuRepo;
    public Menu create(Menu menu){
        return menuRepo.save(menu);
    }
    public Menu getMenuById(Long id){
        return menuRepo.findById(id).get();
    }
    public List<Menu> getMenuByRestaurantId(Long id_restaurant){
        return menuRepo.findByIdRestaurant(id_restaurant);
    }
    public Menu update(Menu menu){
        return menuRepo.save(menu);
    }
    public boolean isExist(Long id){
        return menuRepo.existsById(id);
    }
    public void delete(Long id){
        menuRepo.deleteById(id);
    }
}
