package com.qsol.dashboard.service;

import com.qsol.dashboard.dto.*;
import com.qsol.dashboard.entity.*;
import com.qsol.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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



}
