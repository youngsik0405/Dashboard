package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ess_rack_status_minute")
@Getter
@Setter
@NoArgsConstructor
public class EssRackStatusMinute {

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

    @Column(name = "rack_temperacture")
    private BigDecimal rackTemperature;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
