package com.qsol.dashboard.service;

import com.qsol.dashboard.dto.*;
import com.qsol.dashboard.entity.*;
import com.qsol.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberEssRepository memberEssRepository;
    private final RackStatusRecentRepository rackStatusRecentRepository;
    private final FireStatusRecentRepository fireStatusRecentRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final EssModuleStatusRecentRepository essModuleStatusRecentRepository;
    private final EssCellStatusRecentRepository essCellStatusRecentRepository;
    private final EssMasterRepository essMasterRepository;


    // 대시보드에 필요한 모든 데이터
    public Map<String, Object> getDashboardData(Integer essId) {

        Map<String, Object> result = new HashMap<>();
        result.put("essInfo", getEssInfo(essId));
        result.put("rackStatusInfo", getRackStatusInfo(essId));
        result.put("fireStatusInfo", getFireStatusInfo(essId));
        result.put("moduleInfo", getModuleInfo(essId));
        result.put("eventHistory", getEventHistory(essId));

        return result;
    }

    // Ess 정보
    public EssInfoDto getEssInfo(Integer essId) {

        try {
           EssMaster essMaster = essMasterRepository.findByIdWithJoin(essId);

            if (essMaster == null) {
                return null;
            }

            return EssInfoDto.from(essMaster);

        } catch (Exception e) {
            log.error("EssInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }


    public RackStatusDto getRackStatusInfo(Integer essId) {
        try {
            RackStatusRecent rackStatusRecent = rackStatusRecentRepository.findByEssId(essId);

            if  (rackStatusRecent == null) {
                return null;
            }

            return RackStatusDto.from(rackStatusRecent);

        } catch (Exception e) {
            log.error("RackStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    public FireStatusDto getFireStatusInfo(Integer essId) {
        try {
            FireStatusRecent fireStatusRecent = fireStatusRecentRepository.findByEssId(essId);

            if (fireStatusRecent == null) {
                return null;
            }

            return FireStatusDto.from(fireStatusRecent);
        } catch (Exception e) {
            log.error("FireStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }


    public List<EventHistoryDto> getEventHistory(Integer essId) {
        try {
            return eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId).stream().map(EventHistoryDto::from).toList();
        } catch (Exception e) {
            log.error("EventHistory 조회 실패 essId={}", essId, e);
            return List.of();
        }
    }


    public List<EssModuleStatusDto> getModuleInfo(Integer essId) {
        try {
//            System.out.println(essModuleStatusRecentRepository.findByEssIdWithRack(essId).stream().map(EssModuleStatusDto::from).toList());
            return essModuleStatusRecentRepository.findByEssIdWithRack(essId).stream().map(EssModuleStatusDto::from).toList();
        } catch (Exception e) {
            log.error("ModuleInfo 조회 실패 essId={}", essId, e);
            return List.of();
        }
    }


    public List<EssCellStatusDto> getCellInfo(Integer essId, Integer moduleId) {
        try {
            return essCellStatusRecentRepository.findByEssIdAndModuleIdOrderByCellIdAsc(essId, moduleId).stream().map(EssCellStatusDto::from).toList();
        } catch (Exception e) {
            log.error("CellInfo 조회 실패 essId={}, moduleId={}", essId, moduleId, e);
            return List.of();
        }
    }


    public void testData(Map<String, Object> dashboardData) {
        RackStatusDto rackStatus = (RackStatusDto) dashboardData.get("rackStatusInfo");
        FireStatusDto fireStaus = (FireStatusDto) dashboardData.get("fireStatusInfo");

        // Rack 상태 테스트 데이터
        if (rackStatus != null) {
            rackStatus.setRackSoc(BigDecimal.valueOf(Math.round((80 + Math.random() * 10) * 10) / 10.0));
            rackStatus.setRackDcVoltage(BigDecimal.valueOf(Math.round((700 + Math.random() * 50) * 10) / 10.0));
            rackStatus.setHasAlarm(Math.random() > 0.6); // 40% 확률 알람
        }
        if (fireStaus != null) {
            fireStaus.setFireStatus(Math.random() > 0.6 ? 1 : 0); // 20% 확률 화재
        }

        // Module 테스트 데이터
        List<EssModuleStatusDto> modules = (List<EssModuleStatusDto>) dashboardData.get("moduleInfo");
        if (modules != null) {
            for (EssModuleStatusDto module : modules) {
                module.setModuleDcVoltage(BigDecimal.valueOf(Math.round((33.2 + Math.random() * 0.3) * 100) / 100.0));
                module.setMaxCellVoltage(BigDecimal.valueOf(Math.round((33.3 + Math.random() * 0.1) * 100) / 100.0));
                module.setMinCellVoltage(BigDecimal.valueOf(Math.round((33.1 + Math.random() * 0.1) * 100) / 100.0));
                module.setAvgModuleTemperature(BigDecimal.valueOf(Math.round((20 + Math.random() * 10) * 10) / 10.0));
            }
        }
    }
}
