package com.likelion.staycare.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GoalRequest(
        @NotBlank(message = "목표를 입력해주세요.")
        @Size(min = 2, max = 100, message = "목표는 2~ 100자 이하 입니다.")
        String goal
) {
}
