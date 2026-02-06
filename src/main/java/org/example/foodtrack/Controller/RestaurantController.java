package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/v1/food-diary/restaurant")
public class RestaurantController {

    private final RestaurantImpl restaurantImpl;

    @PostMapping("/add")
    public ResponseEntity<RestaurantResponse> createRestaurant(@RequestBody CreateRestaurantReq createRestaurantReq,
                                                               @RequestParam(defaultValue = "false") boolean force,
                                                               Authentication authentication) {
        String email = authentication.getName();
        log.info("email :{}",email);
        RestaurantResponse restaurantResponse = restaurantImpl.createRestaurant(createRestaurantReq, email,force);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantImpl.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse restaurant = restaurantImpl.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    /**
     *
     * @param q= cuisine,location,name
     * @return RestaurantResponse
     */
    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(
            @RequestParam(required = false) String q) {
        List<RestaurantResponse> restaurants = restaurantImpl.searchRestaurants(q);
        return ResponseEntity.ok(restaurants);
    }
}
