package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EventHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Integer> {
    List<EventHistory> findTop9ByEssIdOrderByEventDtDesc(Integer essId);

    List<EventHistory> findByEssIdAndIdGreaterThanOrderByEventDtDesc(Integer essId, Integer lastEventId);

}
