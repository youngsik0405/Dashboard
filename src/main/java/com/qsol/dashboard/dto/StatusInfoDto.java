package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.FireStatusRecent;
import com.qsol.dashboard.entity.RackStatusRecent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StatusInfoDto {

    private Integer mbmsStatus;
    private BigDecimal rackSoc;
    private BigDecimal rackDcVoltage;
    private BigDecimal rackTemperature;
    private BigDecimal rackCurrent;
    private Boolean hasAlarm;
    private Integer fireStatus;

    public static StatusInfoDto from(RackStatusRecent rackStatusRecent, FireStatusRecent fireStatusRecent) {
        return new StatusInfoDto(
                rackStatusRecent.getMbmsStatus(),
                rackStatusRecent.getRackSoc(),
                rackStatusRecent.getRackDcVoltage(),
                rackStatusRecent.getRackTemperature(),
                rackStatusRecent.getRackCurrent(),
                rackStatusRecent.hasAlarm(),
                fireStatusRecent.getFireStatus()
        );
    }

    public String getMbmsStatusText() {
        return switch (mbmsStatus) {
            case 0 -> "대기 중";
            case 1 -> "충전 중";
            case 2 -> "방전 중";
            case 3 -> "시스템 종료";
            default -> "알수 없음";
        };
    }
}
