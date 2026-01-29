package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Integer> {
    List<EventHistory> findTop9ByEssIdOrderByEventDtDesc(Integer essId);

    List<EventHistory> findByEssIdAndIdGreaterThanOrderByEventDtDesc(Integer essId, Integer lastEventId);

    @Query("select e from EventHistory e " +
            "left join fetch e.essWarningFaultDetailList " +
            "where e.id = :eventId " +
            "and e.essId = :essId")
    EventHistory findByIdAndEssIdWithDetails(@Param("essId") Integer essId, @Param("eventId") Integer eventId);
}
