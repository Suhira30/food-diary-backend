package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DuplicateRestaurantResponse {
    private boolean hasDuplicates;
    private String message;
    private List<PotentialDuplicate> potentialDuplicates;

}
