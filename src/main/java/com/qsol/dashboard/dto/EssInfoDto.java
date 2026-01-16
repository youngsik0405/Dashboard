package com.qsol.dashboard.dto;

import com.qsol.dashboard.entity.EssMaster;
import com.qsol.dashboard.entity.MemberEss;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class EssInfoDto {

    private Integer essId;
    private String essName;
    private String installLocation;
    private Date installDate;
    private String userName;

    public static EssInfoDto from(EssMaster essMaster) {
        return new EssInfoDto(
                essMaster.getId(),
                essMaster.getEssName(),
                essMaster.getInstallLocation(),
                essMaster.getInstallDate(),
                essMaster.getMemberEssList().get(0).getMember().getUserName()
        );
    }

}
