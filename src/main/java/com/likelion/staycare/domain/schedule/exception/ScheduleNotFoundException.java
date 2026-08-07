package com.likelion.staycare.domain.schedule.exception;

import com.likelion.staycare.global.exception.CustomException;

public class ScheduleNotFoundException extends CustomException {

    public ScheduleNotFoundException() {
        super(ScheduleErrorCode.SCHEDULE_NOT_FOUND);
    }
}
