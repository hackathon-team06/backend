package com.likelion.staycare.domain.diagnosis.repository;

import com.likelion.staycare.domain.diagnosis.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findTopByUserIdOrderByDiagnosedAtDesc(Long userId);

    List<Diagnosis> findAllByUserIdOrderByDiagnosedAtDesc(Long userId);
}
