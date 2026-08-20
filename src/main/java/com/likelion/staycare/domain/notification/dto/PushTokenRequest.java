package com.likelion.staycare.domain.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record PushTokenRequest(
        @NotBlank String token
) {
}
