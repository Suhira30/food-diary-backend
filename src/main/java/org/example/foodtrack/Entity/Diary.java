package org.example.foodtrack.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.foodtrack.Dto.Request.CreateDiaryEntryRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "restaurant_id"})
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private Double rating; //out of 5


    @Column(length = 2000)
    private String review;

    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    @Column(name = "is_visited")
    private Boolean isVisited = true;

    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @Column(name = "would_recommend")
    private Boolean wouldRecommend = true;

    private String tags;

    @Column(name = "photo_urls", length = 1000)
    private String photoUrls;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Diary(User user, Restaurant restaurant,CreateDiaryEntryRequest request) {
        this.user = user;
        this.restaurant = restaurant;
        this.rating = request.getRating();
        this.review = request.getReview();
        this.tags = request.getTags();
        this.photoUrls = request.getPhotoUrls();
        this.visitDate = request.getVisitDate()!=null?request.getVisitDate():LocalDateTime.now();
        this.isVisited=true;
        this.isFavorite=request.getIsFavorite()!=null?request.getIsFavorite():false;
        this.wouldRecommend=request.getWouldRecommend()!=null?request.getWouldRecommend(): Boolean.valueOf("N/A");
    }

}