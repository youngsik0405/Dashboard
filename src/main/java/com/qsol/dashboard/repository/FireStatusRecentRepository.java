package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.FireStatusRecent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FireStatusRecentRepository extends JpaRepository<FireStatusRecent, Integer> {
    FireStatusRecent findByEssId(Integer essId);
}
