package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssWarningFaultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EssWarningFaultDetailRepository extends JpaRepository<EssWarningFaultDetail, Integer> {
    EssWarningFaultDetail findByEventId(Integer eventId);
}

