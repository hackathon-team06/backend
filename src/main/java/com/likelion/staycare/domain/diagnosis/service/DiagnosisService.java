package com.likelion.staycare.domain.diagnosis.service;

import com.likelion.staycare.domain.diagnosis.dto.DiagnosisRequest;
import com.likelion.staycare.domain.diagnosis.dto.DiagnosisResponse;
import com.likelion.staycare.domain.diagnosis.entity.CareMotivation;
import com.likelion.staycare.domain.diagnosis.entity.Diagnosis;
import com.likelion.staycare.domain.diagnosis.exception.DiagnosisErrorCode;
import com.likelion.staycare.domain.diagnosis.repository.DiagnosisRepository;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiagnosisService {

    private final UserRepository userRepository;
    private final DiagnosisRepository diagnosisRepository;

    /**
     * 자가진단 실행 (토큰 유저의 결과로 저장)
     */
    @Transactional
    public DiagnosisResponse diagnose(Long userId, DiagnosisRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        Diagnosis diagnosis = Diagnosis.builder()
                .user(user)
                .gender(request.gender())
                .age(request.age())
                .skinType(request.skinType())
                .wakeUpTime(request.wakeUpTime())
                .returnHomeTime(request.returnHomeTime())
                .checkCycle(request.checkCycle())
                .careMotivation(request.careMotivation())
                .build();
        diagnosisRepository.save(diagnosis);

        // User 프로필에도 반영
        user.applyDiagnosisResult(
                request.gender(),
                request.age(),
                request.skinType(),
                request.wakeUpTime(),
                request.returnHomeTime(),
                request.checkCycle(),
                request.careMotivation()
        );

        return DiagnosisResponse.from(diagnosis);
    }

    /**
     * 로그인 유저의 최근 진단 결과
     */
    public DiagnosisResponse getLatest(Long userId) {
        Diagnosis diagnosis = diagnosisRepository
                .findTopByUserIdOrderByDiagnosedAtDesc(userId)
                .orElseThrow(() ->
                        new CustomException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));
        return DiagnosisResponse.from(diagnosis);
    }

    /**
     * 로그인 유저의 진단 이력 전체
     */
    public List<DiagnosisResponse> getMyHistory(Long userId) {
        return diagnosisRepository
                .findAllByUserIdOrderByDiagnosedAtDesc(userId)
                .stream()
                .map(DiagnosisResponse::from)
                .toList();
    }

    /**
     * 특정 진단 상세 조회 (본인 것만)
     */
    public DiagnosisResponse getDiagnosisById(Long userId, Long diagnosisId) {
        Diagnosis diagnosis = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() ->
                        new CustomException(DiagnosisErrorCode.DIAGNOSIS_NOT_FOUND));

        if (!diagnosis.getUser().getId().equals(userId)) {
            throw new CustomException(DiagnosisErrorCode.FORBIDDEN_DIAGNOSIS);
        }

        return DiagnosisResponse.from(diagnosis);
    }

}
