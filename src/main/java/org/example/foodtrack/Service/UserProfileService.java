package org.example.foodtrack.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtrack.Dto.Response.*;
import org.example.foodtrack.Entity.Diary;
import org.example.foodtrack.Entity.User;
import org.example.foodtrack.Exception.NotFoundException;
import org.example.foodtrack.Repo.DiaryRepository;
import org.example.foodtrack.Repo.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryService diaryService;

    public UserProfileResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return buildUserProfile(user);
    }
    private UserProfileResponse buildUserProfile(User user) {
        UserProfileResponse profile = new UserProfileResponse(user);

        long totalReviews = diaryRepository.countByUserId(user.getId());
        long totalFavorites = diaryRepository.countByUserIdAndIsFavoriteTrue(user.getId());
        Double avgRating = diaryRepository.calculateUserAverageRating(user.getId());
        Integer restaurantsCreated = userRepository.countRestaurantsCreatedByUser(user.getId());

        UserStats stats = new UserStats((int) totalReviews, (int) totalFavorites,avgRating,restaurantsCreated);
        profile.setStats(stats);

        log.info("Built profile for user: {}, total reviews: {}", user.getEmail(), totalReviews);
        return profile;
    }

    public UserTimelineResponse getUserTimeline(String email, String sortBy) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserProfileResponse profile = buildUserProfile(user);

        List<Diary> diaryEntries = getDiaryEntriesSorted(user.getId(), sortBy);

        List<DiaryEntryResponse> timeline = diaryEntries.stream()
                .map(this::mapDiaryToResponse)
                .collect(Collectors.toList());

        TimelineStats timelineStats = buildTimelineStats(user.getId(), diaryEntries);

        log.info("Fetched timeline for user: {}, entries: {}", email, timeline.size());

        return new UserTimelineResponse(profile, timeline, timelineStats);
    }
    private List<Diary> getDiaryEntriesSorted(Long userId, String sortBy) {
        if (sortBy == null) {
            sortBy = "latest";
        }

        switch (sortBy.toLowerCase()) {
            case "visit-date":
                return diaryRepository.findByUserIdOrderByVisitDateDesc(userId);
            case "rating":
                return diaryRepository.findByUserIdOrderByRatingDesc(userId);
            case "latest":
            default:
                return diaryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
    }

    private TimelineStats buildTimelineStats(Long userId, List<Diary> entries) {
        TimelineStats stats = new  TimelineStats();

        stats.setTotalEntries(entries.size());

        // Entries this month
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        Integer entriesThisMonth = diaryRepository.countEntriesInDateRange(userId, monthStart, monthEnd);
        stats.setEntriesThisMonth(entriesThisMonth);

        // Entries this year
        LocalDateTime yearStart = LocalDateTime.of(now.getYear(), 1, 1, 0, 0);
        LocalDateTime yearEnd = LocalDateTime.of(now.getYear(), 12, 31, 23, 59, 59);

        Integer entriesThisYear = diaryRepository.countEntriesInDateRange(userId, yearStart, yearEnd);
        stats.setEntriesThisYear(entriesThisYear);

        // Most visited cuisine
        List<String> cuisines = diaryRepository.findMostVisitedCuisines(userId);
        stats.setMostVisitedCuisine(cuisines.isEmpty() ? "N/A" : cuisines.get(0));

        // Most recent visit
        if (!entries.isEmpty() && entries.get(0).getVisitDate() != null) {
            stats.setMostRecentVisit(entries.get(0).getVisitDate().toString());
        } else {
            stats.setMostRecentVisit("N/A");
        }

        return stats;
    }

    public List<DiaryEntryResponse> getUserTimelineFiltered(String email, String filter) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<DiaryEntryResponse> entries = switch (filter.toLowerCase()) {
            case "favorites" -> diaryService.getUserFavorites(email);
            case "top-rated" -> diaryService.getUserTopRated(email);
            case "visited" -> {
                List<Diary> visited = diaryRepository
                        .findByUserIdAndIsVisitedTrueOrderByCreatedAtDesc(user.getId());
                yield visited.stream()
                        .map(this::mapDiaryToResponse)
                        .collect(Collectors.toList());
            }
            default -> diaryService.getUserDiary(email);
        };

        log.info("Filtered timeline for user: {}, filter: {}, count: {}",
                email, filter, entries.size());
        return entries;
    }


    private DiaryEntryResponse mapDiaryToResponse(Diary diary) {
        return new DiaryEntryResponse(diary);
    }

}
