package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.RackStatusRecent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RackStatusRecentRepository extends JpaRepository<RackStatusRecent, Long> {
    RackStatusRecent findByEssId(Integer essId);
}
