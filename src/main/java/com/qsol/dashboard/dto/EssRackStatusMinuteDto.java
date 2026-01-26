package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EssRackStatusMinute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EssRackStatusMinuteDto {
    private Integer essId;
    private Integer rackDeviceId;
    private BigDecimal rackDcVoltage;
    private BigDecimal rackCurrent;
    private BigDecimal rackTemperature;
    private LocalDateTime createdAt;

    public static EssRackStatusMinuteDto from(EssRackStatusMinute essRackStatusMinute) {
        return new EssRackStatusMinuteDto(
               essRackStatusMinute.getEssId(),
               essRackStatusMinute.getRackDeviceId(),
               essRackStatusMinute.getRackDcVoltage(),
               essRackStatusMinute.getRackCurrent(),
               essRackStatusMinute.getRackTemperature(),
               essRackStatusMinute.getCreatedAt()
        );
    }
}
