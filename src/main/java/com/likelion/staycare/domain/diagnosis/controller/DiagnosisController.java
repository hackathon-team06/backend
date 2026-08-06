package com.likelion.staycare.domain.diagnosis.controller;

import com.likelion.staycare.domain.diagnosis.dto.DiagnosisOptionsResponse;
import com.likelion.staycare.domain.diagnosis.dto.DiagnosisRequest;
import com.likelion.staycare.domain.diagnosis.dto.DiagnosisResponse;
import com.likelion.staycare.domain.diagnosis.service.DiagnosisService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Diagnosis", description = "사용자 진단 API")
@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @Operation(summary = "진단 수행(토큰 필요)", description = "사용자의 진단 결과 저장")
    @PostMapping
    public ResponseEntity<DiagnosisResponse> diagnose(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DiagnosisRequest request
    ) {
        return ResponseEntity.ok(
                diagnosisService.diagnose(userDetails.getUserId(), request));
    }

    @Operation(summary = "진단 선택지 조회(토큰 필요X)", description = "진단 화면에서 사용할 선택지 목록 조회")
    @GetMapping("/options")
    public ResponseEntity<DiagnosisOptionsResponse> getOptions() {
        return ResponseEntity.ok(DiagnosisOptionsResponse.getAll());
    }
}
