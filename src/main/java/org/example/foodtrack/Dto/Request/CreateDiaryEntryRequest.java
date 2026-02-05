package org.example.foodtrack.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDiaryEntryRequest {
    private Long restaurantId;
    private Double rating;
    private String review;
    private LocalDateTime visitDate;
    private Boolean isFavorite;
    private Boolean wouldRecommend;
    private String tags;
    private String photoUrls;
}