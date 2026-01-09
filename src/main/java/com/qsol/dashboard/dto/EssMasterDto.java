package com.qsol.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class EssMasterDto {

    private Integer essId;
    private String essName;
    private String installLocation;
    private Date installDate;
    private String userName;
}
