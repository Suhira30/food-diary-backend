package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Dto.Request.CreateDiaryEntryRequest;
import org.example.foodtrack.Dto.Request.UpdateDiaryEntryRequest;
import org.example.foodtrack.Dto.Response.DiaryEntryResponse;
import org.example.foodtrack.Entity.Diary;
import org.example.foodtrack.Entity.Restaurant;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.BadRequestException;
import org.example.foodtrack.Exception.ConflictException;
import org.example.foodtrack.Exception.NotFoundException;
import org.example.foodtrack.Repo.DiaryRepository;
import org.example.foodtrack.Repo.RestaurantRepo;
import org.example.foodtrack.Repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {
    private final UserRepository userRepository;
    private final RestaurantRepo restaurantRepository;
    private final DiaryRepository diaryRepository;
    public DiaryEntryResponse addDiaryEntry(CreateDiaryEntryRequest request, String email) {
        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Find restaurant
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        // Validate rating
        if (request.getRating() == null || request.getRating() < 0.5 || request.getRating() > 5.0) {
            throw new BadRequestException("Rating must be between 0.5 and 5.0");
        }

        // Check if user already reviewed this restaurant
        diaryRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getId())
                .ifPresent(existing -> {
                    throw new ConflictException("You have already reviewed this restaurant. Use update instead.");
                });

        // Create diary entry
        Diary diary = new Diary(user, restaurant,request);
        Diary saved = diaryRepository.save(diary);

        // Update restaurant's average rating
        updateRestaurantRating(restaurant.getId());

        log.info("Diary entry created for user {} and restaurant {}", user.getEmail(), restaurant.getName());

        return mapToResponse(saved);
    }

    private void updateRestaurantRating(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        Double avgRating = diaryRepository.calculateAverageRating(restaurantId);
        long reviewCount = diaryRepository.countByRestaurantId(restaurantId);

        restaurant.setAverageRating(avgRating != null ? avgRating : 0.0);
        restaurant.setTotalReviews((int) reviewCount);

        restaurantRepository.save(restaurant);
        log.info("Updated rating for restaurant {}: avg={}, count={}",
                restaurant.getName(), avgRating, reviewCount);
    }
    public DiaryEntryResponse updateDiaryEntry(Long entryId, UpdateDiaryEntryRequest request, String email) {
        // Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Find diary entry
        Diary diary = diaryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Diary entry not found"));

        // Check ownership
        if (diary.getUser().getId()!= user.getId()) {
            throw new BadRequestException("You can only update your own diary entries");
        }

        // Update fields
        if (request.getRating() != null) {
            if (request.getRating() < 0.5 || request.getRating() > 5.0) {
                throw new BadRequestException("Rating must be between 0.5 and 5.0");
            }
            diary.setRating(request.getRating());
        }
        if (request.getReview() != null) {
            diary.setReview(request.getReview());
        }
        if (request.getVisitDate() != null) {
            diary.setVisitDate(request.getVisitDate());
        }
        if (request.getIsFavorite() != null) {
            diary.setIsFavorite(request.getIsFavorite());
        }
        if (request.getWouldRecommend() != null) {
            diary.setWouldRecommend(request.getWouldRecommend());
        }
        if (request.getTags() != null) {
            diary.setTags(request.getTags());
        }
        if (request.getPhotoUrls() != null) {
            diary.setPhotoUrls(request.getPhotoUrls());
        }

        Diary updated = diaryRepository.save(diary);

        updateRestaurantRating(diary.getRestaurant().getId());

        log.info("Diary entry {} updated by user {}", entryId, user.getEmail());
        return mapToResponse(updated);
    }
    public DiaryEntryResponse toggleFavorite(Long entryId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Diary diary = diaryRepository.findById(entryId)
                .orElseThrow(() -> new NotFoundException("Diary entry not found"));

        if (diary.getUser().getId()!=(user.getId())) {
            throw new BadRequestException("You can only update your own diary entries");
        }

        diary.setIsFavorite(!diary.getIsFavorite());
        Diary updated = diaryRepository.save(diary);

        log.info("Favorite toggled for entry {} by user {}", entryId, user.getEmail());
        return mapToResponse(updated);
    }
    public List<DiaryEntryResponse> getUserDiary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Diary> entries = diaryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        log.info("Fetching diary for user: {}, total entries: {}", email, entries.size());
        return entries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<DiaryEntryResponse> getUserFavorites(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Diary> favorites = diaryRepository
                .findByUserIdAndIsFavoriteTrueOrderByCreatedAtDesc(user.getId());

        return favorites.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    private DiaryEntryResponse mapToResponse(Diary diary) {
        Restaurant restaurant = diary.getRestaurant();
        log.info("DiaryEntryResponse going to be created");
        return new DiaryEntryResponse(diary,restaurant);
    }
}
