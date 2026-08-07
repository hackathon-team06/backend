package com.likelion.staycare.domain.user.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationSettingRequest(
        @NotNull Boolean notificationEnabled
) {
}
