package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EssModuleStatusRecent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class EssModuleStatusDto {

    private Integer moduleId;
    private String batteryModuleStatus;
    private BigDecimal moduleDcVoltage;
    private BigDecimal maxCellVoltage;
    private BigDecimal minCellVoltage;
    private BigDecimal avgModuleTemperature;

    public static EssModuleStatusDto from(EssModuleStatusRecent essModuleStatusRecent) {
        return new EssModuleStatusDto(
                essModuleStatusRecent.getModuleId(),
                essModuleStatusRecent.getStatusText(),
                essModuleStatusRecent.getModuleDcVoltage(),
                essModuleStatusRecent.getMaxCellVoltage(),
                essModuleStatusRecent.getMinCellVoltage(),
                essModuleStatusRecent.getAvgModuleTemperature()
        );
    }
}
