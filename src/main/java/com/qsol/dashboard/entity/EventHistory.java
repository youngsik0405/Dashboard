package com.qsol.dashboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_event_history")
@Getter
@Setter
@NoArgsConstructor
public class EventHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ess_id")
    private Integer essId;

    @Column(name = "event_dt")
    private LocalDateTime eventDt;

    @Column(name = "event_desc")
    private String eventDesc;
}
