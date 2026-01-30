package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;


@Entity
@Slf4j
@Table(name = "tb_ess_rack_status_recent")
@Getter
@Setter
@NoArgsConstructor
public class RackStatusRecent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_number")
    private Long idNumber;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "rack_device_id")
    private Integer rackDeviceId;

    @Column(name = "mbms_status")
    private Integer mbmsStatus;

    @Column(name = "rack_soc")
    private BigDecimal rackSoc;

    @Column(name = "rack_dc_voltage")
    private BigDecimal rackDcVoltage;

    @Column(name = "rack_temperature")
    private BigDecimal rackTemperature;

    @Column(name = "rack_current")
    private BigDecimal rackCurrent;

    // 알람
    @Column(name = "rack_voltage_under_warning")
    private Integer rackVoltageUnderWarning;

    @Column(name = "rack_voltage_over_warning")
    private Integer rackVoltageOverWarning;

    @Column(name = "rack_temperature_under_warning")
    private Integer rackTemperatureUnderWarning;

    @Column(name = "rack_temperature_over_warning")
    private Integer rackTemperatureOverWarning;

    @Column(name = "rack_current_over_warning")
    private Integer rackCurrentOverWarning;

    @Column(name = "rack_voltage_under_fault1")
    private Integer rackVoltageUnderFault1;

    @Column(name = "rack_voltage_over_fault1")
    private Integer rackVoltageOverFault1;

    @Column(name = "rack_temperature_under_fault1")
    private Integer rackTemperatureUnderFault1;

    @Column(name = "rack_temperature_over_fault1")
    private Integer rackTemperatureOverFault1;

    @Column(name = "rack_current_over_fault1")
    private Integer rackCurrentOverFault1;

    @Column(name = "module_bms_communication_fault1")
    private Integer moduleBmsCommunicationFault1;

    @Column(name = "rack_inner_adc_fault1")
    private Integer rackInnerAdcFault1;

    @Column(name = "component_func_fault1")
    private Integer componentFuncFault1;

    @Column(name = "stl_check_error_fault1")
    private Integer stlCheckErrorFault1;

    @Column(name = "rack_voltage_cell_voltage_total_mismatch_fault1")
    private Integer rackVoltageCellVoltageTotalMismatchFault1;


    // 랙 상태 메세지
    public String getMbmsStatusText() {
        if (mbmsStatus == null) {
            return "-";
        }

        switch (mbmsStatus) {
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
                Integer.valueOf(1).equals(rackVoltageUnderWarning) ||
                Integer.valueOf(1).equals(rackVoltageOverWarning) ||
                Integer.valueOf(1).equals(rackTemperatureUnderWarning) ||
                Integer.valueOf(1).equals(rackTemperatureOverWarning) ||
                Integer.valueOf(1).equals(rackCurrentOverWarning) ||
                Integer.valueOf(1).equals(rackVoltageUnderFault1) ||
                Integer.valueOf(1).equals(rackVoltageOverFault1) ||
                Integer.valueOf(1).equals(rackTemperatureUnderFault1) ||
                Integer.valueOf(1).equals(rackTemperatureOverFault1) ||
                Integer.valueOf(1).equals(rackCurrentOverFault1) ||
                Integer.valueOf(1).equals(moduleBmsCommunicationFault1) ||
                Integer.valueOf(1).equals(rackInnerAdcFault1) ||
                Integer.valueOf(1).equals(componentFuncFault1) ||
                Integer.valueOf(1).equals(stlCheckErrorFault1) ||
                Integer.valueOf(1).equals(rackVoltageCellVoltageTotalMismatchFault1);

        return hasAlarm;
    }
}
