package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.foodtrack.Entity.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private Boolean isPro;
    private LocalDateTime createdAt;
    private UserStats stats;

    public UserProfileResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.isPro = user.getIsPro();
        this.createdAt = user.getCreatedAt();
    }
}