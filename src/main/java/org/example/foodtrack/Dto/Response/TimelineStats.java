package org.example.foodtrack.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TimelineStats {
    private Integer totalEntries;
    private Integer entriesThisMonth;
    private Integer entriesThisYear;
    private String mostVisitedCuisine;
    private String mostRecentVisit;
}
