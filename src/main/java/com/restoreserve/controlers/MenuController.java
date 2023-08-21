package com.restoreserve.controlers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.CreateMenuDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateMenuDto;
import com.restoreserve.model.entities.Menu;
import com.restoreserve.model.entities.Restaurant;
import com.restoreserve.services.MenuService;
import com.restoreserve.services.RestaurantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private ModelMapper modelMapper;
    @PostMapping("/create")
    public ResponseEntity<ResponseData<Menu>> create(@Valid @RequestBody CreateMenuDto menuDto){
        ResponseData<Menu> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            Restaurant dataRestaurant = restaurantService.getRestaurantById(menuDto.getRestaurant());
            if(dataRestaurant!=null){
                Menu menu = modelMapper.map(menuDto, Menu.class);
                menu.setRestaurant(dataRestaurant);
                dataResponse.setPayload(menuService.create(menu));
                dataResponse.getMessage().add("Success Add Menu");
                dataResponse.setStatus(true);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @GetMapping("/all/{id}")
    public ResponseEntity<ResponseData<List<Menu>>> getAllMenuByIdRestaurant(@PathVariable Long id){
        ResponseData<List<Menu>> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            dataResponse.setPayload(menuService.getMenuByRestaurantId(id));
            dataResponse.getMessage().add("success get all menu");
            dataResponse.setStatus(true);
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
             dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Menu>> getMenuById(@PathVariable Long id){
        ResponseData<Menu> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            dataResponse.setPayload(menuService.getMenuById(id));
            dataResponse.getMessage().add("success get menu");
            dataResponse.setStatus(true);
            return ResponseEntity.ok(dataResponse);
        } catch (Exception e) {
             dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @PutMapping("/update")
    public ResponseEntity<ResponseData<Menu>> update(@Valid @RequestBody UpdateMenuDto menuDto){
        ResponseData<Menu> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            Restaurant dataRestaurant = restaurantService.getRestaurantById(menuDto.getRestaurant());
            if(dataRestaurant!=null){
                Menu dataMenu = modelMapper.map(menuDto, Menu.class);
                dataMenu.setRestaurant(dataRestaurant);
                dataResponse.setPayload(menuService.update(dataMenu));
                dataResponse.getMessage().add("Success Update Menu");
                dataResponse.setStatus(true);
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseData<?>> delete(@PathVariable Long id){
        ResponseData<?> dataResponse= new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if(menuService.isExist(id)){
                menuService.delete(id);
                dataResponse.setStatus(true);
                dataResponse.getMessage().add("success delete menu");
                return ResponseEntity.ok(dataResponse);
            }
            dataResponse.getMessage().add("Menu with that id not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }
}
