package org.example.foodtrack.Dto.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInfo {
    private Long id;
    private String name;
    private String email;
    private Boolean isPro;
}
