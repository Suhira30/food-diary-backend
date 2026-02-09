package org.example.foodtrack.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateCheckoutRequest {

    @NotBlank(message = "Plan is required")
    @Pattern(regexp = "MONTHLY|YEARLY", message = "Plan must be MONTHLY or YEARLY")
    private String plan;
}