package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssCellStatusRecent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EssCellStatusRecentRepository extends JpaRepository<EssCellStatusRecent, Long> {
    List<EssCellStatusRecent> findByEssIdAndModuleIdOrderByCellId(Integer essId, Integer moduleId);
}
