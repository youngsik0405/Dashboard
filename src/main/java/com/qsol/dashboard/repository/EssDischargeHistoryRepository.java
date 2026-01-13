package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssDischargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EssDischargeHistoryRepository extends JpaRepository<EssDischargeHistory, Integer> {

    List<EssDischargeHistory> findTop9ByEssIdOrderByCreatedAtDesc(Integer essId);
}
