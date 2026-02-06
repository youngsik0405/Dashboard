package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;

@Entity
@Slf4j
@Table(name = "tb_ess_module_status_recent")
@Getter
@Setter
@NoArgsConstructor
public class EssModuleStatusRecent {

    @Id
    @Column(name = "id_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNumber;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "rack_device_id")
    private Integer rackDeviceId;

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

    // 알람
    @Column(name = "cell_voltage_under_warning")
    private Integer cellVoltageUnderWarning;

    @Column(name = "cell_voltage_over_warning")
    private Integer cellVoltageOverWarning;

    @Column(name = "cell_voltage_max_deviation_over_warning")
    private Integer cellVoltageMaxDeviationOverWarning;

    @Column(name = "module_temperature_under_warning")
    private Integer moduleTemperatureUnderWarning;

    @Column(name = "module_temperature_over_warning")
    private Integer moduleTemperatureOverWarning;

    @Column(name = "module_temperature_max_deviation_over_warning")
    private Integer moduleTemperatureMaxDeviationOverWarning;

    @Column(name = "cell_voltage_under_fault1")
    private Integer cellVoltageUnderFault1;

    @Column(name = "cell_voltage_over_fault1")
    private Integer cellVoltageOverFault1;

    @Column(name = "cell_voltage_max_deviation_over_fault1")
    private Integer cellVoltageMaxDeviationOverFault1;

    @Column(name = "module_temperature_under_fault1")
    private Integer moduleTemperatureUnderFault1;

    @Column(name = "module_temperature_over_fault1")
    private Integer moduleTemperatureOverFault1;

    @Column(name = "module_temperature_max_deviation_over_fault1")
    private Integer moduleTemperatureMaxDeviationOverFault1;

    // 모듈 상태 메세지
    public String getModuleStatusText() {
        if (batteryModuleStatus == null){
            return "-";
        }

        switch (batteryModuleStatus) {
            case 0 : return "대기 중";
            case 1 : return "충전 중";
            case 2 : return "방전 중";
            case 3 : return "연결 끊김";
            default: return "알수없음";
        }
    }

    // 알람 유무
    public boolean hasAlarm() {

        boolean hasAlarm =
                Integer.valueOf(1).equals(cellVoltageUnderWarning) ||
                Integer.valueOf(1).equals(cellVoltageOverWarning) ||
                Integer.valueOf(1).equals(cellVoltageMaxDeviationOverWarning) ||
                Integer.valueOf(1).equals(moduleTemperatureUnderWarning) ||
                Integer.valueOf(1).equals(moduleTemperatureOverWarning) ||
                Integer.valueOf(1).equals(moduleTemperatureMaxDeviationOverWarning) ||
                Integer.valueOf(1).equals(cellVoltageUnderFault1) ||
                Integer.valueOf(1).equals(cellVoltageOverFault1) ||
                Integer.valueOf(1).equals(cellVoltageMaxDeviationOverFault1) ||
                Integer.valueOf(1).equals(moduleTemperatureUnderFault1) ||
                Integer.valueOf(1).equals(moduleTemperatureOverFault1) ||
                Integer.valueOf(1).equals(moduleTemperatureMaxDeviationOverFault1);

        return hasAlarm;
    }

}
