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
public class RackStatusDto {

    private String mbmsStatus;
    private BigDecimal rackSoc;
    private BigDecimal rackDcVoltage;
    private BigDecimal rackTemperature;
    private BigDecimal rackCurrent;
    private Boolean hasAlarm;

    public static RackStatusDto from(RackStatusRecent rackStatusRecent) {
        return new RackStatusDto(
                rackStatusRecent.getMbmsStatusText(),
                rackStatusRecent.getRackSoc(),
                rackStatusRecent.getRackDcVoltage(),
                rackStatusRecent.getRackTemperature(),
                rackStatusRecent.getRackCurrent(),
                rackStatusRecent.hasAlarm()
        );
    }
}
