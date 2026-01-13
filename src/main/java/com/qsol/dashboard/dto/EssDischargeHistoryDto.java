package com.qsol.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EssDischargeHistoryDto {

    private LocalDateTime startTime;
    private LocalDateTime endTime;


}
