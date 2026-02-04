package org.example.foodtrack.Repo;

import org.example.foodtrack.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(r) FROM Restaurant r WHERE r.createdBy.id = :userId")
    Integer countRestaurantsCreatedByUser(@Param("userId") Long userId);
}
