package com.likelion.staycare.domain.stamp.controller;

import com.likelion.staycare.domain.stamp.dto.MyPageStampSummaryResponse;
import com.likelion.staycare.domain.stamp.dto.StampCalendarResponse;
import com.likelion.staycare.domain.stamp.service.StampService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stamp", description = "스탬프 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stamps")
public class StampController {

    private final StampService stampService;

    @Operation(
            summary = "월별 스탬프 달력 조회",
            description = "특정 연도/월의 날짜별 스탬프 상태와 월별 포인트 요약을 조회합니다."
    )
    @GetMapping("/calendar")
    public ResponseEntity<StampCalendarResponse> getStampCalendar(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "조회 연도", example = "2026")
            @RequestParam Integer year,

            @Parameter(description = "조회 월", example = "8")
            @RequestParam Integer month
    ) {
        return ResponseEntity.ok(
                stampService.getStampCalendar(userDetails.getUserId(), year, month)
        );
    }

    @Operation(
            summary = "마이페이지 스탬프북 카드 조회",
            description = "사용자의 전체 스탬프북 카드 개수, 완료된 카드 개수, 카드 목록을 조회합니다."
    )
    @GetMapping("/books")
    public ResponseEntity<MyPageStampSummaryResponse> getMyPageStampSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                stampService.getMyPageStampSummary(userDetails.getUserId())
        );
    }
}
