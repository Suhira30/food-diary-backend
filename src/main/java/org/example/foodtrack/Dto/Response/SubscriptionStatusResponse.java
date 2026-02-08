package org.example.foodtrack.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubscriptionStatusResponse {
    private Boolean isPro;
    private String plan;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime currentPeriodEnd;
    private String stripeSubscriptionId;
    private Boolean canCancel;
    private Boolean cancelAtPeriodEnd;
}