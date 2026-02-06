package org.example.foodtrack.Controller;
import lombok.RequiredArgsConstructor;
import org.example.foodtrack.Dto.Response.DiaryEntryResponse;
import org.example.foodtrack.Dto.Response.UserProfileResponse;
import org.example.foodtrack.Dto.Response.UserTimelineResponse;
import org.example.foodtrack.Service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/food-diary/profile")
public class ProfileController {
    private final UserProfileService userProfileService;

    /**
     *
     * @param authentication=authentication
     * @return UserProfileResponse
     */
    @GetMapping("/get/user-profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserProfileResponse profile = userProfileService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    /**
     * @param sortBy defaultValue = "latest"
     * @param authentication authentication
     * @return UserTimelineResponse
     */

    @GetMapping("/timeline")
    public ResponseEntity<UserTimelineResponse> getUserTimeline(
            @RequestParam(required = false, defaultValue = "latest") String sortBy,
            Authentication authentication) {
        String email = authentication.getName();
        UserTimelineResponse timeline = userProfileService.getUserTimeline(email, sortBy);
        return ResponseEntity.ok(timeline);
    }
    /**
     * Get filtered timeline
     * GET /v1/food-diary/profile/timeline/filter?type=favorites
     * Filter types:
     * - all: All entries
     * - favorites: Only favorites
     * - top-rated: 4+ stars
     * - visited: Only visited restaurants
     * {{baseURL}}/v1/food-diary/profile/timeline/filter?type=favorites
     * {{baseURL}}/v1/food-diary/profile/timeline/filter?type=top-rated
     */
    @GetMapping("/timeline/filter")
    public ResponseEntity<List<DiaryEntryResponse>> getFilteredTimeline(
            @RequestParam(required = false, defaultValue = "all") String type,
            Authentication authentication) {
        String email = authentication.getName();
        List<DiaryEntryResponse> entries = userProfileService.getUserTimelineFiltered(email, type);
        return ResponseEntity.ok(entries);
    }
}
