package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="tb_ess_master")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EssMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ess_name")
    private String essName;

    @Column(name = "install_location")
    private String installLocation;

    @Column(name = "install_date")
    private String installDate;

}
