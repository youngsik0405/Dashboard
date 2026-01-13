package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ess_discharge_history")
@Getter
@Setter
@NoArgsConstructor
public class EssDischargeHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "discharge_type")
    private String dischargeType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
