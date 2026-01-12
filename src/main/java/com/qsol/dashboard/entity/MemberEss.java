package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="tb_member_ess")
@Getter
@Setter
@NoArgsConstructor
public class MemberEss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_number")
    private Long idNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "ess_id")
    private EssMaster essMaster;


}
