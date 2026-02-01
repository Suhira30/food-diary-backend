package org.example.foodtrack.Controller;

import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Request.CreateDiaryEntryRequest;
import org.example.foodtrack.Dto.Request.UpdateDiaryEntryRequest;
import org.example.foodtrack.Dto.Response.DiaryEntryResponse;
import org.example.foodtrack.Dto.Response.FoodDiaryResponse;
import org.example.foodtrack.Service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary/diary")
public class DairyController {
    private final DiaryService diaryService;

    @PostMapping("/add")
    public ResponseEntity<DiaryEntryResponse> addDiaryEntry(
            @RequestBody CreateDiaryEntryRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        DiaryEntryResponse diaryEntryResponse = diaryService.addDiaryEntry(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(diaryEntryResponse);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<DiaryEntryResponse> updateDiaryEntry(
            @PathVariable Long id,
            @RequestBody UpdateDiaryEntryRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        DiaryEntryResponse response = diaryService.updateDiaryEntry(id, request, email);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/toggle-favorite/{id}")
    public ResponseEntity<DiaryEntryResponse> toggleFavorite(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        DiaryEntryResponse response = diaryService.toggleFavorite(id, email);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/user-diary")
    public ResponseEntity<List<DiaryEntryResponse>> getUserDiary(Authentication authentication) {
        String email = authentication.getName();
        List<DiaryEntryResponse> diary = diaryService.getUserDiary(email);
        return ResponseEntity.ok(diary);
    }
    @GetMapping("/favorites")
    public ResponseEntity<List<DiaryEntryResponse>> getUserFavorites(Authentication authentication) {
        String email = authentication.getName();
        List<DiaryEntryResponse> favorites = diaryService.getUserFavorites(email);
        return ResponseEntity.ok(favorites);
    }
    @GetMapping("/top-rated")
    public ResponseEntity<List<DiaryEntryResponse>> getUserTopRated(Authentication authentication) {
        String email = authentication.getName();
        List<DiaryEntryResponse> topRated = diaryService.getUserTopRated(email);
        return ResponseEntity.ok(topRated);
    }
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DiaryEntryResponse>> getRestaurantReviews(
            @PathVariable Long restaurantId) {
        List<DiaryEntryResponse> reviews = diaryService.getRestaurantReviews(restaurantId);
        return ResponseEntity.ok(reviews);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<FoodDiaryResponse> deleteDiaryEntry(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        diaryService.deleteDiaryEntry(id, email);
        return ResponseEntity.ok(new FoodDiaryResponse(
                "Diary entry deleted successfully",
                HttpStatus.OK.value()
        ));
    }
}
