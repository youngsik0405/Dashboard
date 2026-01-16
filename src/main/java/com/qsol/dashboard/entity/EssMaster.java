package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="tb_ess_master")
@Getter
@Setter
@NoArgsConstructor
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
    private Date installDate;

    @OneToMany
    @JoinColumn(name = "ess_id")
    private List<MemberEss> memberEssList = new ArrayList<>();

}
