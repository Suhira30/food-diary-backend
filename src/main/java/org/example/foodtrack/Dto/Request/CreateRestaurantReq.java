package org.example.foodtrack.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CreateRestaurantReq {
    private String name;
    private String description;
    private String location;
    private String cuisine;
    private String imageUrl;
}
