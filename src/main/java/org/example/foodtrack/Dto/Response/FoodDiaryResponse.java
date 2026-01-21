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

    public FoodDiaryResponse(String token, String message, int statusCode) {
        this.data = token;
        this.message = message;
        this.statusCode = statusCode;
    }

    public FoodDiaryResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
