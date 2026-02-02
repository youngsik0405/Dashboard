package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EssRackStatusHistory;
import com.qsol.dashboard.entity.RackStatusRecent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EssRackStatusHistoryDto {

    private Integer essId;
    private Integer rackDeviceId;
    private BigDecimal rackDcVoltage;
    private BigDecimal rackCurrent;
    private BigDecimal rackTemperature;
    private LocalDateTime createdAt;

    public static EssRackStatusHistoryDto from (EssRackStatusHistory essRackStatusHistory) {
        return new EssRackStatusHistoryDto(
                essRackStatusHistory.getEssId(),
                essRackStatusHistory.getRackDeviceId(),
                essRackStatusHistory.getRackDcVoltage(),
                essRackStatusHistory.getRackCurrent(),
                essRackStatusHistory.getRackTemperature(),
                essRackStatusHistory.getCreatedAt()
        );
    }
}
