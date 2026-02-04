package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserStats {
    private Integer totalRestaurantsVisited;
    private Integer totalReviews;
    private Integer totalFavorites;
    private Double averageRating;
    private Integer restaurantsCreated;
    public UserStats( Integer totalReview,Integer  totalFavorites,Double avgRating,Integer restaurantsCreated){
        this.totalRestaurantsVisited = totalReview;
        this.totalReviews = totalReview;
        this.totalFavorites = totalFavorites;
        this.averageRating = avgRating!=null?Math.round(avgRating*10.0)/10.0: 0.0;
        this.restaurantsCreated = restaurantsCreated != null ? restaurantsCreated : 0;
    }
}
