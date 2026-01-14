package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "tb_ess_cell_status_recent")
@Getter
@Setter
@NoArgsConstructor
public class EssCellStatusRecent {

    @Id
    @Column(name = "id_number")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "rack_device_id")
    private Integer rackDeviceId;

    @Column(name = "module_id")
    private Integer moduleId;

    @Column(name = "cell_id")
    private Integer cellId;

    @Column(name = "voltage")
    private BigDecimal voltage;
}
