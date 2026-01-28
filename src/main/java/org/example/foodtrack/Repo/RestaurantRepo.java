package org.example.foodtrack.Repo;

import org.example.foodtrack.Entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepo extends JpaRepository<Restaurant,Long> {
    // Check exact duplicate (normalized name + location)
    Optional<Restaurant> findByNormalizedNameAndNormalizedLocation(
            String normalizedName, String normalizedLocation);

    // Find similar restaurants by normalized name
    @Query("SELECT r FROM Restaurant r WHERE r.normalizedName LIKE %:normalizedName%")
    List<Restaurant> findSimilarByNormalizedName(@Param("normalizedName") String normalizedName);

    // Advanced search
    @Query("SELECT r FROM Restaurant r WHERE " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Restaurant> searchRestaurants(@Param("searchTerm") String searchTerm);

    // Get all restaurants ordered by newest first
    List<Restaurant> findAllByOrderByCreatedAtDesc();

    // Find restaurants in same location
    List<Restaurant> findByNormalizedLocation(String normalizedLocation);

    // Search by name only
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
