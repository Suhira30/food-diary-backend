package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserTimelineResponse {
    private UserProfileResponse profile;
    private List<DiaryEntryResponse> timeline;
    private TimelineStats stats;
}