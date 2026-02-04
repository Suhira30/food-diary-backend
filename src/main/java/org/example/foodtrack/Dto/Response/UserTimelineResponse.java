package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class UserTimelineResponse {
    private UserProfileResponse profile;
    private List<DiaryEntryResponse> timeline;
    private TimelineStats stats;
}