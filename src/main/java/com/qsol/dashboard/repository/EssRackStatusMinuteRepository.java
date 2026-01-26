package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssRackStatusMinute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EssRackStatusMinuteRepository extends JpaRepository<EssRackStatusMinute, Long> {
    List<EssRackStatusMinute> findByEssId(Integer essId);
}
