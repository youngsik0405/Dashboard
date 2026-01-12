package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_ess_fire_status_recent")
@Getter
@Setter
@NoArgsConstructor

public class FireStatusRecent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "fire_status")
    private Integer fireStatus;
}
