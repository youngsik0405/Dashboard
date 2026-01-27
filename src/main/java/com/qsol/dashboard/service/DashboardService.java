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
    private final EssRackStatusMinuteRepository essRackStatusMinuteRepository;


    // 대시보드에 필요한 모든 데이터
    public Map<String, Object> getDashboardData(Integer essId) {

        Map<String, Integer> sizeMap = new HashMap<>();
        sizeMap.put("eventHistorySize", getEventHistory(essId) == null ? 0 : getEventHistory(essId).size());
        sizeMap.put("moduleSize", getModuleInfo(essId) == null ? 0 : getModuleInfo(essId).size());

        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("essInfo", getEssInfo(essId));
        dashboardData.put("rackStatusInfo", getRackStatusInfo(essId));
        dashboardData.put("fireStatusInfo", getFireStatusInfo(essId));
        dashboardData.put("moduleInfo", getModuleInfo(essId));
        dashboardData.put("eventHistory", getEventHistory(essId));
        dashboardData.put("sizeMap", sizeMap);

        return dashboardData;
    }

    // Ess 정보
    public EssInfoDto getEssInfo(Integer essId) {

        try {
           EssMaster essMaster = essMasterRepository.findByIdWithJoin(essId);
           return essMaster == null ? null : EssInfoDto.from(essMaster);
        } catch (Exception e) {
            log.error("EssInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }


    public RackStatusDto getRackStatusInfo(Integer essId) {
        try {
            RackStatusRecent rackStatusRecent = rackStatusRecentRepository.findByEssId(essId);
            return rackStatusRecent == null ? null : RackStatusDto.from(rackStatusRecent);
        } catch (Exception e) {
            log.error("RackStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    public FireStatusDto getFireStatusInfo(Integer essId) {
        try {
            FireStatusRecent fireStatusRecent = fireStatusRecentRepository.findByEssId(essId);
            return fireStatusRecent == null ? null : FireStatusDto.from(fireStatusRecent);
        } catch (Exception e) {
            log.error("FireStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }


    public List<EventHistoryDto> getEventHistory(Integer essId) {
        try {
            List<EventHistoryDto> eventHistoryList = eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId).stream().map(EventHistoryDto::from).toList();
            return eventHistoryList.isEmpty() ? null : eventHistoryList;
        } catch (Exception e) {
            log.error("EventHistory 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    public EventHistoryDto getEventDetail(Integer essId, Integer eventId) {
        try {
            EventHistory eventHistory = eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc();
        }
    }


    public List<EssModuleStatusDto> getModuleInfo(Integer essId) {
        try {
            List<EssModuleStatusDto> essModuleStatusList = essModuleStatusRecentRepository.findByEssIdWithRack(essId).stream().map(EssModuleStatusDto::from).toList();
            return essModuleStatusList.isEmpty() ? null : essModuleStatusList;
        } catch (Exception e) {
            log.error("ModuleInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }


    public List<EssCellStatusDto> getCellInfo(Integer essId, Integer moduleId) {
        try {
            List<EssCellStatusDto> essCellStatusList = essCellStatusRecentRepository.findByEssIdAndModuleIdOrderByCellIdAsc(essId, moduleId).stream().map(EssCellStatusDto::from).toList();
            return essCellStatusList.isEmpty() ? null : essCellStatusList;
        } catch (Exception e) {
            log.error("CellInfo 조회 실패 essId={}, moduleId={}", essId, moduleId, e);
            return null;
        }
    }

    public List<EssRackStatusMinuteDto> getEssRackStatusMinuteData(Integer essId, Integer rackDeviceId) {
        try {
            // 현재 시점을 기준으로 6시간 전
            LocalDateTime sixHoursAgo = LocalDateTime.now().minusHours(6);
            List<EssRackStatusMinuteDto> essRackStatusMinuteList = essRackStatusMinuteRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, sixHoursAgo).stream().map(EssRackStatusMinuteDto::from).toList();
            return essRackStatusMinuteList.isEmpty() ? null : essRackStatusMinuteList;
        } catch (Exception e) {
            log.error("EssRackStatusMinuteData 조회 실패 essId={}", essId, e);
            return null;
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
            fireStaus.setFireStatus(Math.random() > 0.6 ? 1 : 0); // 40% 확률 화재
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
