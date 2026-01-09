package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="tb_member_ess")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberEss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_number")
    private Long idNumber;

    @OneToOne
    @Column(name = "user_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "ess_id")
    private EssMaster essMaster;

}
