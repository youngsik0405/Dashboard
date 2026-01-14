package com.qsol.dashboard.repository;


import com.qsol.dashboard.entity.EssModuleStatusRecent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EssModuleStatusRecentRepository extends JpaRepository<EssModuleStatusRecent, Long> {
    @Query("SELECT m FROM EssModuleStatusRecent m " +
            "JOIN m.rackStatusRecent r " +
            "WHERE r.essMaster.id = :essId")
    List<EssModuleStatusRecent> findByEssIdWithRack(Integer essId);
}
