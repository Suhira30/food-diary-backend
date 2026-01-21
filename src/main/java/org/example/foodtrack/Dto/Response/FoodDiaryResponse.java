package org.example.foodtrack.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodDiaryResponse {

    private Object data;
    private String message;
    private int statusCode;
    private String token;

    public FoodDiaryResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public FoodDiaryResponse(String message, int value, String token) {
        this.token = token;
        this.message = message;
        this.statusCode = value;
    }
}
