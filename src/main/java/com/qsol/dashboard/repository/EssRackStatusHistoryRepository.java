package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssRackStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EssRackStatusHistoryRepository extends JpaRepository<EssRackStatusHistory, Long> {
    List<EssRackStatusHistory> findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(Integer essId, Integer rackDeviceId, LocalDateTime createdAt);
}
