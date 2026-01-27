package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.example.foodtrack.Dto.Request.CreateRestaurantReq;
import org.example.foodtrack.Dto.Response.RestaurantResponse;
import org.example.foodtrack.Service.RestaurantImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary")
public class RestaurantController {

    private final RestaurantImpl restaurantImpl;

    @PostMapping("/add/restaurant")
    public ResponseEntity<RestaurantResponse> createRestaurant(@RequestBody CreateRestaurantReq createRestaurantReq,
                                                               @RequestParam(defaultValue = "false") boolean force,
                                                               Authentication authentication) {
        String email = authentication.getName();
        RestaurantResponse restaurantResponse = restaurantImpl.createRestaurant(createRestaurantReq, email,force);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }

    @GetMapping("/all/restaurant")
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantImpl.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }


    @GetMapping("/restaurant/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse restaurant = restaurantImpl.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }
}
