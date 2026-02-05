package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.foodtrack.Entity.Diary;
import org.example.foodtrack.Entity.Restaurant;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryEntryResponse {
    private Long id;//personal dairy entry id
    private Long restaurantId;
    private String restaurantName;
    private String restaurantLocation;
    private String restaurantCuisine;
    private String restaurantImageUrl;
    private Double rating;
    private String review;
    private LocalDateTime visitDate;
    private Boolean isVisited;
    private Boolean isFavorite;
    private Boolean wouldRecommend;
    private String tags;
    private String photoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DiaryEntryResponse(Diary diary, Restaurant restaurant) {
        this.id = diary.getId();
        this.restaurantId = restaurant.getId();
        this.restaurantName = restaurant.getName();
        this.restaurantLocation = restaurant.getLocation();
        this.restaurantCuisine = restaurant.getCuisine();
        this.restaurantImageUrl = restaurant.getImageUrl();
        this.rating = diary.getRating();
        this.review = diary.getReview();
        this.visitDate = diary.getVisitDate();
        this.isVisited = diary.getIsVisited();
        this.isFavorite = diary.getIsFavorite();
        this.wouldRecommend = diary.getWouldRecommend();
        this.tags = diary.getTags();
        this.photoUrls = diary.getPhotoUrls();
        this.createdAt = diary.getCreatedAt();
        this.updatedAt = diary.getUpdatedAt();
    }

    public DiaryEntryResponse(Diary diary) {
        this.id = diary.getId();
        this.restaurantId = diary.getRestaurant().getId();
        this.restaurantName = diary.getRestaurant().getName();
        this.restaurantLocation = diary.getRestaurant().getLocation();
        this.restaurantCuisine = diary.getRestaurant().getCuisine();
        this.restaurantImageUrl = diary.getRestaurant().getImageUrl();
        this.rating = diary.getRating();
        this.review = diary.getReview();
        this.visitDate = diary.getVisitDate();
        this.isVisited = diary.getIsVisited();
        this.isFavorite = diary.getIsFavorite();
        this.wouldRecommend = diary.getWouldRecommend();
        this.tags = diary.getTags();
        this.photoUrls = diary.getPhotoUrls();
        this.createdAt = diary.getCreatedAt();
        this.updatedAt = diary.getUpdatedAt();
    }
}