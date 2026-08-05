package com.likelion.staycare.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinType {
    DRY("건성"),
    OILY("지성"),
    COMBINATION("복합성"),
    DEHYDRATED("수분부족지성"),
    NORMAL("중성");

    private final String description;
}
