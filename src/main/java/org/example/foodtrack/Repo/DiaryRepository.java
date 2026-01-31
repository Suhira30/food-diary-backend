package org.example.foodtrack.Repo;

import org.example.foodtrack.Entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
