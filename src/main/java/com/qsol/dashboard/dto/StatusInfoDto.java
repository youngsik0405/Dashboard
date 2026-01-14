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

    private String mbmsStatus;
    private BigDecimal rackSoc;
    private BigDecimal rackDcVoltage;
    private BigDecimal rackTemperature;
    private BigDecimal rackCurrent;
    private Boolean hasAlarm;
    private Integer fireStatus;

    public static StatusInfoDto from(RackStatusRecent rackStatusRecent, FireStatusRecent fireStatusRecent) {
        return new StatusInfoDto(
                rackStatusRecent != null ? rackStatusRecent.getMbmsStatusText() : "-",
                rackStatusRecent != null ? rackStatusRecent.getRackSoc() : null,
                rackStatusRecent != null ? rackStatusRecent.getRackDcVoltage() : null,
                rackStatusRecent != null ? rackStatusRecent.getRackTemperature() : null,
                rackStatusRecent != null ? rackStatusRecent.getRackCurrent() : null,
                rackStatusRecent != null && rackStatusRecent.hasAlarm(),
                fireStatusRecent != null ? fireStatusRecent.getFireStatus() : 0
        );
    }

}
