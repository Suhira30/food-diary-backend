package org.example.foodtrack.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PotentialDuplicate {
    private Long id;
    private String name;
    private String location;
    private double similarityScore;
    private String createdByName;
}
