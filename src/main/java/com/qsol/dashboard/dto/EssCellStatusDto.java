package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EssCellStatusRecent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class EssCellStatusDto {

    private Integer moduleId;
    private Integer cellId;
    private BigDecimal voltage;

    public static EssCellStatusDto from(EssCellStatusRecent essCellStatusRecent) {
        return new EssCellStatusDto(
                essCellStatusRecent.getModuleId(),
                essCellStatusRecent.getCellId(),
                essCellStatusRecent.getVoltage()
        );
    }
}
