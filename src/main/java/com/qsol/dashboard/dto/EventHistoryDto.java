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

    private LocalDateTime eventDt;
    private String eventDesc;

    public static EventHistoryDto from(EventHistory eventHistory) {
        return new EventHistoryDto(
                eventHistory.getEventDt(),
                eventHistory.getEventDesc()
        );
    }
}
