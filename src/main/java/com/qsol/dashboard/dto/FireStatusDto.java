package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.FireStatusRecent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FireStatusDto {

    private Integer fireStatus;

    public static FireStatusDto from(FireStatusRecent fireStatusRecent) {
        return new FireStatusDto(
                fireStatusRecent.getFireStatus()
        );
    }
}
