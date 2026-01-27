package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssRackStatusMinute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EssRackStatusMinuteRepository extends JpaRepository<EssRackStatusMinute, Long> {
    // 특정 시간 이후의 데이터를 시간 순으로 조회
    List<EssRackStatusMinute> findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(Integer essId, Integer rackDeviceId, LocalDateTime createdAt);
}
