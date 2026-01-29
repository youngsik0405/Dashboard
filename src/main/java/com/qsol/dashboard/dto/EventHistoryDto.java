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
    private String eventType;
    private LocalDateTime eventDt;
    private String eventDesc;
    private String eventDetail;

    public static EventHistoryDto from(EventHistory eventHistory) {

        return new EventHistoryDto(
                eventHistory.getId(),
                eventHistory.getEventType(),
                eventHistory.getEventDt(),
                eventHistory.getEventDesc(),
                null
        );
    }
}
