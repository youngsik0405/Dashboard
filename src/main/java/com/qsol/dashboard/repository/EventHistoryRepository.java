package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Integer> {
    List<EventHistory> findTop9ByEssIdOrderByEventDtDesc(Integer essId);

    @Query("select e from EventHistory e " +
            "left join fetch e.essWarningFaultDetailList " +
            "where e.id = :eventId " +
            "and e.essId = :essId")
    EventHistory findByIdAndEssIdWithDetails(Integer essId, Integer eventId);
}
