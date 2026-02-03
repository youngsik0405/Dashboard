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

    private final RackStatusRecentRepository rackStatusRecentRepository;
    private final FireStatusRecentRepository fireStatusRecentRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final EssModuleStatusRecentRepository essModuleStatusRecentRepository;
    private final EssCellStatusRecentRepository essCellStatusRecentRepository;
    private final EssMasterRepository essMasterRepository;
//    private final EssRackStatusMinuteRepository essRackStatusMinuteRepository;
    private final EssWarningFaultDetailRepository essWarningFaultDetailRepository;

    private final EssRackStatusHistoryRepository essRackStatusHistoryRepository;


    // 대시보드에 필요한 모든 데이터
    public Map<String, Object> getDashboardData(Integer essId, Integer lastEventId) {

        EssInfoDto essInfo = getEssInfo(essId);
        RackStatusDto rackStatus = getRackStatusInfo(essId);
        FireStatusDto fireStatus = getFireStatusInfo(essId);
        List<EssModuleStatusDto> moduleInfo = getModuleInfo(essId);
        List<EventHistoryDto> eventHistory = getEventHistory(essId, lastEventId);

        Map<String, Integer> sizeMap = new HashMap<>();
        sizeMap.put("eventHistorySize", eventHistory == null ? 0 : eventHistory.size());
        sizeMap.put("moduleSize", moduleInfo == null ? 0 : moduleInfo.size());

        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("essInfo", essInfo);
        dashboardData.put("rackStatusInfo", rackStatus);
        dashboardData.put("fireStatusInfo", fireStatus);
        dashboardData.put("moduleInfo", moduleInfo);
        dashboardData.put("eventHistory", eventHistory);
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


    public List<EventHistoryDto> getEventHistory(Integer essId, Integer lastEventId) {
        try {
            List<EventHistory> eventList;

            if (lastEventId != null) {
                eventList = eventHistoryRepository.findByEssIdAndIdGreaterThanOrderByEventDtDesc(essId, lastEventId);
            } else {
                eventList = eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId);
            }

            List<EventHistoryDto> eventHistoryList = eventList.stream().map(EventHistoryDto::from).toList();
            return eventHistoryList.isEmpty() ? null : eventHistoryList;
        } catch (Exception e) {
            log.error("EventHistory 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    public EssWarningFaultDetailDto getEventDetail(Integer eventId) {
        try {
            EssWarningFaultDetail essWarningFaultDetail = essWarningFaultDetailRepository.findByEventId(eventId);
            return essWarningFaultDetail == null ? null : EssWarningFaultDetailDto.from(essWarningFaultDetail);
        } catch (Exception e) {
            log.error("EventDetail 조회 실패 eventId={}", eventId, e);
            return null;
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

    public List<EssRackStatusHistoryDto> getEssRackStatusHistoryData(Integer essId, Integer rackDeviceId) {
        try {
            // 현재 시점을 기준으로 1시간 전
            LocalDateTime hourAgo = LocalDateTime.now().minusHours(1);
            List<EssRackStatusHistoryDto> essRackStatusHistoryList = essRackStatusHistoryRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, hourAgo).stream().map(EssRackStatusHistoryDto::from).toList();
            return essRackStatusHistoryList.isEmpty() ? null : essRackStatusHistoryList;
        } catch (Exception e) {
            log.error("EssRackStatusMinuteData 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    public List<EssRackStatusHistoryDto> getLatestRackStatus(Integer essId,Integer rackDeviceId, LocalDateTime lastCreatedAt) {
        try {
          List<EssRackStatusHistoryDto> essRackStatusHistoryList = essRackStatusHistoryRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, lastCreatedAt).stream().map(EssRackStatusHistoryDto::from).toList();
          return essRackStatusHistoryList.isEmpty() ? null : essRackStatusHistoryList;
        } catch (Exception e) {
            log.error("LatestRackStatus 조회 실패 essId={}", essId, e);
            return null;
        }
    }
}
