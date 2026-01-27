package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EventHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EventHistoryDto {
    private Integer Id;
    private LocalDateTime eventDt;
    private String eventDesc;
    private Integer eventType;
    private String eventDetail;

    public static EventHistoryDto from(EventHistory eventHistory) {
        Integer eventType = null;
        String eventDetail = null;

        // warningFaultDetails가 null이 아니고 비어있지 않을 때만 접근
        if (eventHistory.getEssWarningFaultDetailList() != null && !eventHistory.getEssWarningFaultDetailList().isEmpty()) {
            eventType = eventHistory.getEssWarningFaultDetailList().get(0).getEventType();
            eventDetail = eventHistory.getEssWarningFaultDetailList().get(0).getEventDetail();
        }


        return new EventHistoryDto(
                eventHistory.getId(),
                eventHistory.getEventDt(),
                eventHistory.getEventDesc(),
                eventType,
                eventDetail
        );
    }
}
