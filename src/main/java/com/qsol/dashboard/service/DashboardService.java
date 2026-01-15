package com.qsol.dashboard.service;

import com.qsol.dashboard.dto.*;
import com.qsol.dashboard.entity.*;
import com.qsol.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final MemberEssRepository memberEssRepository;
    private final RackStatusRecentRepository rackStatusRecentRepository;
    private final FireStatusRecentRepository fireStatusRecentRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final EssModuleStatusRecentRepository essModuleStatusRecentRepository;
    private final EssCellStatusRecentRepository essCellStatusRecentRepository;

    // Ess 정보
    @Transactional(readOnly = true)
    public EssInfoDto getEssInfo(Integer essId) {

        try {
            MemberEss memberEss = memberEssRepository.findByEssMaster_Id(essId);

            if (memberEss == null) {
                return null;
            }

            return EssInfoDto.from(memberEss);

        } catch (Exception e) {
            log.error("EssInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public StatusInfoDto getRackStatusInfo(Integer essId) {
        try {
            RackStatusRecent rackStatusRecent = rackStatusRecentRepository.findByEssMaster_Id(essId);
            FireStatusRecent fireStatusRecent = fireStatusRecentRepository.findByEssId(essId);

            if  (rackStatusRecent == null && fireStatusRecent == null) {
                return null;
            }

            return StatusInfoDto.from(rackStatusRecent, fireStatusRecent);

        } catch (Exception e) {
            log.error("RackStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<EventHistoryDto> getEventHistory(Integer essId) {
        try {
            return eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId).stream().map(EventHistoryDto::from).toList();
        } catch (Exception e) {
            log.error("EventHistory 조회 실패 essId={}", essId, e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<EssModuleStatusDto> getModuleInfo(Integer essId) {
        try {
            return essModuleStatusRecentRepository.findByEssIdWithRack(essId).stream().map(EssModuleStatusDto::from).toList();
        } catch (Exception e) {
            log.error("ModuleInfo 조회 실패 essId={}", essId, e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<EssCellStatusDto> getCellInfo(Integer essId, Integer moduleId) {
        try {
            return essCellStatusRecentRepository.findByEssIdAndModuleIdOrderByCellId(essId, moduleId).stream().map(EssCellStatusDto::from).toList();
        } catch (Exception e) {
            log.error("CellInfo 조회 실패 essId={}, moduleId={}", essId, moduleId, e);
            return List.of();
        }
    }


}
