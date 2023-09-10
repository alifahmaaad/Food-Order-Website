package com.restoreserve.controlers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restoreserve.dto.CreateMenuDto;
import com.restoreserve.dto.ResponseData;
import com.restoreserve.dto.UpdateMenuDto;
import com.restoreserve.enums.RoleEnum;
import com.restoreserve.model.entities.Menu;
import com.restoreserve.model.entities.Restaurant;
import com.restoreserve.model.entities.User;
import com.restoreserve.security.ImplementUserDetails.CustomUserDetails;
import com.restoreserve.services.ImageService;
import com.restoreserve.services.MenuService;
import com.restoreserve.services.RestaurantService;
import com.restoreserve.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ImageService imageService;

    // restaurant, when id_resto on menu same as id user in auth
    @PostMapping("/restaurant/create")
    public ResponseEntity<ResponseData<Menu>> create(@Valid @ModelAttribute CreateMenuDto menuDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println("masukkkk bang");
        ResponseData<Menu> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            Restaurant dataRestaurant = restaurantService.getRestaurantById(menuDto.getRestaurant());
            if (dataRestaurant != null) {
                if (isUserAllowedToAccessThisEndpoint(userDetails)
                        || dataRestaurant.getUserOwner().getId().equals(userDetails.getId())) {
                    // need to check cause only user it self can update except his role superadmin
                    // or appadmin
                    Menu menu = modelMapper.map(menuDto, Menu.class);
                    menu.setRestaurant(dataRestaurant);
                    if (!menuDto.getPhoto().isEmpty()) {
                        String imagePath = imageService.saveImage(menuDto.getPhoto(), "menu");
                        menu.setPhoto(imagePath);
                    } else {
                        menu.setPhoto("uploads/menu/default.png");
                    }
                    dataResponse.setPayload(menuService.create(menu));
                    dataResponse.getMessage().add("Success Add Menu");
                    dataResponse.setStatus(true);
                    return ResponseEntity.ok(dataResponse);
                }
                dataResponse.getMessage().add("You are not authorized to Create menu in this restaurant");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }

    // all
    @GetMapping("/all/{id}")
    public ResponseEntity<ResponseData<List<Menu>>> getAllMenuByIdRestaurant(@PathVariable Long id) {
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

    // all
    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<Menu>> getMenuById(@PathVariable Long id) {
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

    // restaurant, when id_resto on menu same as id user in auth
    @PutMapping("/restaurant/update")
    public ResponseEntity<ResponseData<Menu>> update(@Valid @ModelAttribute UpdateMenuDto menuDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<Menu> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            Restaurant dataRestaurant = restaurantService.getRestaurantById(menuDto.getRestaurant());
            if (dataRestaurant != null) {
                if (isUserAllowedToAccessThisEndpoint(userDetails)
                        || dataRestaurant.getUserOwner().getId().equals(userDetails.getId())) {
                    // need to check cause only user it self can update except his role superadmin
                    // or appadmin
                    Menu menuPrev = menuService.getMenuById(menuDto.getId());
                    Menu dataMenu = modelMapper.map(menuDto, Menu.class);
                    dataMenu.setRestaurant(dataRestaurant);
                    if (!menuDto.getPhoto().isEmpty()) {
                        if (menuPrev.getPhoto() != "uploads/menu/default.png") {
                            imageService.deleteImage(menuPrev.getPhoto());
                        }
                        String imagePath = imageService.saveImage(menuDto.getPhoto(), "menu");
                        dataMenu.setPhoto(imagePath);
                    } else {
                        dataMenu.setPhoto(menuPrev.getPhoto());
                    }
                    dataResponse.setPayload(menuService.update(dataMenu));
                    dataResponse.getMessage().add("Success Update Menu");
                    dataResponse.setStatus(true);
                    return ResponseEntity.ok(dataResponse);
                }
                dataResponse.getMessage().add("You are not authorized to Update menu in this resataurant");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            dataResponse.getMessage().add("Restaurant not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }

    // restaurant, when id_resto on menu same as id user in auth
    @DeleteMapping("/restaurant/delete/{id}")
    public ResponseEntity<ResponseData<?>> delete(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseData<?> dataResponse = new ResponseData<>(false, new ArrayList<>(), null);
        try {
            if (menuService.isExist(id)) {
                Restaurant dataRestaurant = restaurantService
                        .getRestaurantById(menuService.getMenuById(id).getRestaurant().getId());
                if (isUserAllowedToAccessThisEndpoint(userDetails)
                        || dataRestaurant.getUserOwner().getId().equals(userDetails.getId())) {
                    // need to check cause only user it self can update except his role superadmin
                    // or appadmin
                    menuService.delete(id);
                    dataResponse.setStatus(true);
                    dataResponse.getMessage().add("success delete menu");
                    return ResponseEntity.ok(dataResponse);
                }
                dataResponse.getMessage().add("You are not authorized to delete menu in this resataurant");
                return ResponseEntity.badRequest().body(dataResponse);
            }
            dataResponse.getMessage().add("Menu with that id not found");
            return ResponseEntity.badRequest().body(dataResponse);
        } catch (Exception e) {
            dataResponse.getMessage().add(e.getMessage());
            return ResponseEntity.badRequest().body(dataResponse);
        }
    }

    private boolean isUserAllowedToAccessThisEndpoint(CustomUserDetails userDetails) {
        User userData = userService.findByUsername(userDetails.getUsername());
        return userData.getRole().equals(RoleEnum.Super_Admin) || userData.getRole().equals(RoleEnum.App_Admin);
    }
}
