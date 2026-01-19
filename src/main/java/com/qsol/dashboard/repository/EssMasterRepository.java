package com.qsol.dashboard.repository;

import com.qsol.dashboard.entity.EssMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EssMasterRepository extends JpaRepository<EssMaster, Integer> {

    @Query("SELECT em FROM EssMaster em " +
            "LEFT JOIN em.memberEssList mel " +
            "LEFT JOIN mel.member " +
            "WHERE em.id = :essId")
    EssMaster findByIdWithJoin(Integer essId);

}
