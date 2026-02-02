package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

@Entity
@Table(name = "tb_ess_rack_status_history")
@Getter
@Setter
@NoArgsConstructor
public class EssRackStatusHistory {

    @Id
    @Column(name = "id_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNumber;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "rack_device_id")
    private Integer rackDeviceId;

    @Column(name = "rack_dc_voltage")
    private BigDecimal rackDcVoltage;

    @Column(name = "rack_current")
    private BigDecimal rackCurrent;

    @Column(name = "rack_temperature")
    private BigDecimal rackTemperature;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
