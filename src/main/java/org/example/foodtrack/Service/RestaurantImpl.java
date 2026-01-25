package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Request.CreateRestaurantReq;
import org.example.foodtrack.Dto.Response.DuplicateRestaurantResponse;
import org.example.foodtrack.Dto.Response.PotentialDuplicate;
import org.example.foodtrack.Dto.Response.RestaurantResponse;
import org.example.foodtrack.Entity.Restaurant;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.ConflictException;
import org.example.foodtrack.Exception.Handler.ForbiddenException;
import org.example.foodtrack.Exception.NotFoundException;
import org.example.foodtrack.Repo.RestaurantRepo;
import org.example.foodtrack.Repo.UserRepository;
import org.example.foodtrack.Util.StringSimilarityUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantImpl {

    private final UserRepository userRepository;
    private final StringSimilarityUtil similarityUtil;
    private final RestaurantRepo restaurantRepository;
    private static final double SIMILARITY_THRESHOLD = 85.0;

    public RestaurantResponse createRestaurant(CreateRestaurantReq createRestaurantReq, String email,boolean force) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getIsPro() == null || !user.getIsPro()) {
            throw new ForbiddenException("Only Pro users can create restaurants. Please upgrade to Pro!");
        }
        if (createRestaurantReq.getName() == null || createRestaurantReq.getName().isEmpty()) {
            throw new BadRequestException("Restaurant name is required");
        }
        if (createRestaurantReq.getLocation() == null || createRestaurantReq.getLocation().isBlank()) {
            throw new BadRequestException("Restaurant location is required");
        }
        if (!force) {
            DuplicateRestaurantResponse duplicateCheck = checkDuplicates(createRestaurantReq);
            if (duplicateCheck.isHasDuplicates()) {
                throw new ConflictException(
                        "Similar restaurants found: " +
                                duplicateCheck.getPotentialDuplicates().stream()
                                        .map(d -> d.getName() + " at " + d.getLocation())
                                        .collect(Collectors.joining(", "))
                );
            }
        }

        Restaurant restaurant = new Restaurant(createRestaurantReq, user);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return mapToResponse(savedRestaurant);

    }

    public DuplicateRestaurantResponse checkDuplicates(CreateRestaurantReq request) {
        List<PotentialDuplicate> duplicates = new ArrayList<>();  // Now uses separate class

        String normalizedName = similarityUtil.normalize(request.getName());
        String normalizedLocation = similarityUtil.normalize(request.getLocation());

        // Check for exact match
        restaurantRepository.findByNormalizedNameAndNormalizedLocation(
                        normalizedName, normalizedLocation)
                .ifPresent(r -> duplicates.add(new PotentialDuplicate(
                        r.getId(),
                        r.getName(),
                        r.getLocation(),
                        100.0,
                        r.getCreatedByName()
                )));

        // Find similar restaurants
        List<Restaurant> similarRestaurants = restaurantRepository
                .findSimilarByNormalizedName(normalizedName);

        for (Restaurant restaurant : similarRestaurants) {
            double nameSimilarity = similarityUtil.calculateSimilarity(
                    request.getName(), restaurant.getName());
            double locationSimilarity = similarityUtil.calculateSimilarity(
                    request.getLocation(), restaurant.getLocation());

            double avgSimilarity = (nameSimilarity + locationSimilarity) / 2;

            if (avgSimilarity >= SIMILARITY_THRESHOLD) {
                boolean alreadyAdded = duplicates.stream()
                        .anyMatch(d -> d.getId().equals(restaurant.getId()));

                if (!alreadyAdded) {
                    duplicates.add(new PotentialDuplicate(
                            restaurant.getId(),
                            restaurant.getName(),
                            restaurant.getLocation(),
                            Math.round(avgSimilarity * 10.0) / 10.0,
                            restaurant.getCreatedByName()
                    ));
                }
            }
        }

        if (!duplicates.isEmpty()) {
            return new DuplicateRestaurantResponse(
                    true,
                    "Similar restaurants found. Please check if your restaurant already exists.",
                    duplicates
            );
        }

        return new DuplicateRestaurantResponse(false, "No duplicates found", null);
    }
    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse(restaurant);
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setDescription(restaurant.getDescription());
        response.setLocation(restaurant.getLocation());
        response.setCuisine(restaurant.getCuisine());
        response.setImageUrl(restaurant.getImageUrl());
        response.setCreatedByName(restaurant.getCreatedByName());
        response.setCreatedById(restaurant.getCreatedBy() != null ? restaurant.getCreatedBy().getId() : null);
        response.setAverageRating(restaurant.getAverageRating());
        response.setTotalReviews(restaurant.getTotalReviews());
        response.setCreatedAt(restaurant.getCreatedAt());
        return response;
    }
}
