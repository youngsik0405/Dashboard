package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.MemberEss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberEssRepository extends JpaRepository<MemberEss, Long> {
    MemberEss findByEssMaster_Id(Integer essId);
}
