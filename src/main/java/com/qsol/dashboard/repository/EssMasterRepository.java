package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EssMasterRepository extends JpaRepository<EssMaster, Integer> {

    @Query("SELECT em from EssMaster em " +
            "")
    EssMaster findByIdWithMembers(Integer essId);

}
