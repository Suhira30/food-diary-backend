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
public class DuplicateRestaurantResponse {
    private boolean hasDuplicates;
    private String message;
    private List<PotentialDuplicate> potentialDuplicates;

}
