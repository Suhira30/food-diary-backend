package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Request.CreateRestaurantReq;
import org.example.foodtrack.Dto.Response.DuplicateRestaurantResponse;
import org.example.foodtrack.Dto.Response.RestaurantResponse;
import org.example.foodtrack.Entity.Restaurant;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.Handler.ForbiddenException;
import org.example.foodtrack.Exception.NotFoundException;
import org.example.foodtrack.Repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantImpl {

    private final UserRepository userRepository;

    public RestaurantResponse createRestaurant(CreateRestaurantReq createRestaurantReq, String email,boolean force) {

        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not founded")));
        if (user.get().getIsPro() == null || !user.get().getIsPro()) {
            throw new ForbiddenException("Only Pro users can create restaurants.Please upgrade to pro");
        }
        if (createRestaurantReq.getName() == null || createRestaurantReq.getName().isEmpty()) {
            throw new BadRequestException("Restaurant name is required");
        }
        if (createRestaurantReq.getLocation() == null || createRestaurantReq.getLocation().isBlank()) {
            throw new BadRequestException("Restaurant location is required");
        }
//        if (!force) {
//            DuplicateRestaurantResponse duplicateCheck = checkDuplicates(request);
//            if (duplicateCheck.isHasDuplicates()) {
//                throw new ConflictException(
//                        "Similar restaurants found: " +
//                                duplicateCheck.getPotentialDuplicates().stream()
//                                        .map(d -> d.getName() + " at " + d.getLocation())
//                                        .collect(Collectors.joining(", "))
//                );
//            }
//        }

        Restaurant restaurant = new Restaurant(createRestaurantReq, user.get());
        return null;

    }
}
