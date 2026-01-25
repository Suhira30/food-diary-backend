package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.foodtrack.Entity.Restaurant;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String cuisine;
    private String imageUrl;
    private String createdByName;
    private Long createdById;
    private Double averageRating;
    private Integer totalReviews;
    private LocalDateTime createdAt;

    public RestaurantResponse(Restaurant restaurant) {
        this.id=restaurant.getId();
        this.name=restaurant.getName();
        this.description=restaurant.getDescription();
        this.location=restaurant.getLocation();
        this.cuisine=restaurant.getCuisine();
        this.imageUrl=restaurant.getImageUrl();
        this.createdByName=restaurant.getCreatedByName();
        this.createdById=restaurant.getId();
        this.averageRating=restaurant.getAverageRating();
        this.totalReviews=restaurant.getTotalReviews();
        this.createdAt=restaurant.getCreatedAt();
    }
}
