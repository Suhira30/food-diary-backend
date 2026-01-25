package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.foodtrack.Dto.Request.CreateRestaurantReq;
import org.example.foodtrack.Dto.Response.RestaurantResponse;
import org.example.foodtrack.Service.RestaurantImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary")
public class RestaurantController {

    private final RestaurantImpl restaurantImpl;

    @PostMapping("/add/restaurant")
    public ResponseEntity<RestaurantResponse> createRestaurant(CreateRestaurantReq createRestaurantReq, Authentication authentication, @RequestParam(defaultValue = "false") boolean force) {
        String email = authentication.name();
        RestaurantResponse restaurantResponse = restaurantImpl.createRestaurant(createRestaurantReq, email,force);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantResponse);
    }

}
