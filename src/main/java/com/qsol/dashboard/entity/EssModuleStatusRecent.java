package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_ess_module_status_recent")
@Getter
@Setter
@NoArgsConstructor
public class EssModuleStatusRecent {

    @Id
    @Column(name = "id_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNumber;

    @Column(name = "ess_id", insertable = false, updatable = false)
    private Integer essId;

    @Column(name = "rack_device_id", insertable = false, updatable = false)
    private Integer rackDeviceId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ess_id", referencedColumnName = "ess_id"),
            @JoinColumn(name = "rack_device_id", referencedColumnName = "rack_device_id")
    })
    private RackStatusRecent rackStatusRecent;

    @Column(name = "module_id")
    private Integer moduleId;

    @Column(name = "battery_module_status")
    private Integer batteryModuleStatus;

    @Column(name = "module_dc_voltage")
    private BigDecimal moduleDcVoltage;

    @Column(name = "max_cell_voltage")
    private BigDecimal maxCellVoltage;

    @Column(name = "min_cell_voltage")
    private BigDecimal minCellVoltage;

    @Column(name = "avg_module_temperature")
    private BigDecimal avgModuleTemperature;

    public String getStatusText() {
        if (batteryModuleStatus == null){
            return "-";
        }

        switch (batteryModuleStatus) {
            case 0 : return "대기중";
            case 1 : return "충전중";
            case 2 : return "방전중";
            case 3 : return "시스템 종료";
            default: return "알수없음";
        }
    }

}
