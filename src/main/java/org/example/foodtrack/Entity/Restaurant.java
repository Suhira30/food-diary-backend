package org.example.foodtrack.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.foodtrack.Dto.Request.CreateRestaurantReq;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "restaurants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "location"})
        },
        indexes = {
                @Index(name = "idx_restaurant_name", columnList = "name"),
                @Index(name = "idx_restaurant_location", columnList = "location")
        })
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Normalized name for duplicate detection (lowercase, no special chars)
    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String location;

    // Normalized location for duplicate detection
    @Column(name = "normalized_location", nullable = false)
    private String normalizedLocation;

    private String cuisine;

    @Column(name = "image_url")
    private String imageUrl;

    // Remove this if you don't have User relationship yet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Helper method to set normalized values
    @PrePersist
    @PreUpdate
    private void normalizeFields() {
        if (this.name != null) {
            this.normalizedName = normalizeString(this.name);
        }
        if (this.location != null) {
            this.normalizedLocation = normalizeString(this.location);
        }
    }

    private String normalizeString(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public Restaurant(CreateRestaurantReq createRestaurantReq,User user) {
        this.name = createRestaurantReq.getName();
        this.description = createRestaurantReq.getDescription();
        this.location = createRestaurantReq.getLocation();
        this.cuisine = createRestaurantReq.getCuisine();
        this.imageUrl = createRestaurantReq.getImageUrl();
        this.createdBy=user;
        this.createdByName=user.getName();


    }
}
