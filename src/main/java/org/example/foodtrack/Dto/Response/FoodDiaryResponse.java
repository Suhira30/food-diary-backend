package org.example.foodtrack.Dto.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Don't include null fields in JSON
public class FoodDiaryResponse {
    private String message;
    private Integer statusCode;
    private String data;

    public FoodDiaryResponse(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

//    public FoodDiaryResponse(String message, int value, String token) {
//        this.data = token;
//        this.message = message;
//        this.statusCode = value;
//    }
}
