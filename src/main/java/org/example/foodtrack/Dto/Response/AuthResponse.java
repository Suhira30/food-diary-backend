package org.example.foodtrack.Dto.Response;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String message;
    private Integer statusCode;
    private String token;
    private UserInfo user;

    public AuthResponse(String message, int value) {
        this.message = message;
        this.statusCode = value;
    }
}
