package org.example.foodtrack.Repo;

import org.example.foodtrack.Entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Long> {
    Optional<Diary> findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    Long countByRestaurantId(Long restaurantId);

    @Query("SELECT AVG(d.rating) FROM Diary d WHERE d.restaurant.id = :restaurantId")
    Double calculateAverageRating(@Param("restaurantId") Long restaurantId);

    List<Diary> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Diary> findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(Long userId);

    List<Diary> findByUserIdAndRatingGreaterThanEqualOrderByRatingDesc(
            Long userId, Double minRating);

    List<Diary> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    List<Diary> findByUserIdAndIsVisitedTrueOrderByCreatedAtDesc(Long userId);

    long countByUserId(Long userId);

    long countByUserIdAndIsFavoriteTrue(Long userId);

    @Query("SELECT AVG(d.rating) FROM Diary d WHERE d.user.id = :userId AND d.isVisited = true")
    Double calculateUserAverageRating(@Param("userId") Long userId);

    @Query("SELECT COUNT(d) FROM Diary d WHERE d.user.id = :userId " +
            "AND d.visitDate >= :startDate AND d.visitDate <= :endDate")
    Integer countEntriesInDateRange(@Param("userId") Long userId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    List<Diary> findByUserIdOrderByVisitDateDesc(Long userId);

    List<Diary> findByUserIdOrderByRatingDesc(Long userId);

    @Query("SELECT d.restaurant.cuisine FROM Diary d WHERE d.user.id = :userId " +
            "GROUP BY d.restaurant.cuisine ORDER BY COUNT(d) DESC")
    List<String> findMostVisitedCuisines(@Param("userId") Long userId);

}
