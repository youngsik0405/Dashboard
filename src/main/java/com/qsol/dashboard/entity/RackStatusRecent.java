package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.InterfaceAddress;

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

    @ManyToOne
    @JoinColumn(name = "ess_id")
    private EssMaster essMaster;

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

    // 알람 유무
    public boolean hasAlarm() {

        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            String fieldName = field.getName();

            // warning 또는 fault1 으로 끝나는 컬럼만 체크
            if (fieldName.endsWith("Warning") || fieldName.endsWith("Fault1")) {

                try {
                    field.setAccessible(true);
                    Object value = field.get(this);

                    // 값이 1이면 알람 있음
                    if (value instanceof Integer && ((Integer) value) == 1) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    log.warn("접근 실패", fieldName, e);
                }
            }
        }
        return false;
    }
}
