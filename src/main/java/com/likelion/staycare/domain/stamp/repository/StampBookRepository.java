package com.likelion.staycare.domain.stamp.repository;

import com.likelion.staycare.domain.stamp.entity.StampBook;
import com.likelion.staycare.domain.stamp.entity.StampBookStatus;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StampBookRepository extends JpaRepository<StampBook, Long> {

    List<StampBook> findAllByStatusAndEndDateBefore(StampBookStatus status, LocalDate date);

    List<StampBook> findAllByUserOrderByStartDateAsc(User user);
}
