package com.likelion.staycare.domain.mission.entity;

import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "morning_routine_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_morning_routine_item_order",
                columnNames = {"morning_routine_id", "item_order"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MorningRoutineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "morning_routine_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morning_routine_id", nullable = false)
    private MorningRoutine morningRoutine;

    @Column(nullable = false, length = 300)
    private String content;

    @Column(length = 100)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MorningRoutineItemSource source;

    @Column(name = "item_order", nullable = false)
    private int itemOrder;

    @Builder
    public MorningRoutineItem(
            MorningRoutine morningRoutine,
            String content,
            String category,
            MorningRoutineItemSource source,
            int itemOrder
    ) {
        this.morningRoutine = morningRoutine;
        this.content = content;
        this.category = category;
        this.source = source;
        this.itemOrder = itemOrder;
    }
}
