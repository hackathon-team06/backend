package com.likelion.staycare.domain.diagnosis.service;

import com.likelion.staycare.domain.diagnosis.dto.DiagnosisRequest;
import com.likelion.staycare.domain.diagnosis.dto.DiagnosisResponse;
import com.likelion.staycare.domain.diagnosis.entity.Diagnosis;
import com.likelion.staycare.domain.diagnosis.exception.DiagnosisErrorCode;
import com.likelion.staycare.domain.diagnosis.repository.DiagnosisRepository;
import com.likelion.staycare.domain.point.service.PointService;
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
    private final PointService pointService;

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

        // 진단 완료 1회 보상
        pointService.rewardDiagnosisComplete(user);

        return DiagnosisResponse.from(diagnosis);
    }

}
