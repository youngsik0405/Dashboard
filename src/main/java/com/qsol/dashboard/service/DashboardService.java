package com.qsol.dashboard.service;

import com.qsol.dashboard.dto.EssInfoDto;
import com.qsol.dashboard.dto.EventHistoryDto;
import com.qsol.dashboard.dto.EssModuleStatusDto;
import com.qsol.dashboard.dto.StatusInfoDto;
import com.qsol.dashboard.entity.*;
import com.qsol.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
            log.error("ESS 정보 조회 중 시스템 오류", e);
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
            log.error("Rack 정보 조회 중 시스템 오류", e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<EventHistoryDto> getEventHistory(Integer essId) {
        List<EventHistory> eventHistoryList = eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId);
        return eventHistoryList.stream().map(EventHistoryDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<EssModuleStatusDto> getModuleInfo(Integer essId) {
        List<EssModuleStatusRecent> essModuleStatusRecentList = essModuleStatusRecentRepository.findByEssIdWithRack(essId);
        return essModuleStatusRecentList.stream().map(EssModuleStatusDto::from).toList();
    }


}
