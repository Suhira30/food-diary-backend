package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.foodtrack.Dto.Request.CreateRestaurantReq;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
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

}
