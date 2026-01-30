package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EssWarningFaultDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EssWarningFaultDetailDto {
    private Integer eventId;
    private String eventDetail;

    public static EssWarningFaultDetailDto from(EssWarningFaultDetail essWarningFaultDetail) {
        return new EssWarningFaultDetailDto(
                essWarningFaultDetail.getEventId(),
                essWarningFaultDetail.getEventDetail()
        );
    }
}
