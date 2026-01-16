package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssMaster;
import com.qsol.dashboard.entity.MemberEss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberEssRepository extends JpaRepository<MemberEss, Long> {
    MemberEss findByEssId(Integer essId);
}
