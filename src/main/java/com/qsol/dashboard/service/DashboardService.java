package com.qsol.dashboard.service;

import com.qsol.dashboard.dto.EssInfoDto;
import com.qsol.dashboard.dto.EventHistoryDto;
import com.qsol.dashboard.dto.StatusInfoDto;
import com.qsol.dashboard.entity.EventHistory;
import com.qsol.dashboard.entity.FireStatusRecent;
import com.qsol.dashboard.entity.MemberEss;
import com.qsol.dashboard.entity.RackStatusRecent;
import com.qsol.dashboard.repository.EventHistoryRepository;
import com.qsol.dashboard.repository.FireStatusRecentRepository;
import com.qsol.dashboard.repository.MemberEssRepository;
import com.qsol.dashboard.repository.RackStatusRecentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final MemberEssRepository memberEssRepository;
    private final RackStatusRecentRepository rackStatusRecentRepository;
    private final FireStatusRecentRepository fireStatusRecentRepository;
    private final EventHistoryRepository eventHistoryRepository;

    // Ess 정보
    @Transactional(readOnly = true)
    public EssInfoDto getEssInfo(Integer essId) {

        MemberEss memberEss = memberEssRepository.findByEssMaster_Id(essId);

        if (memberEss == null) {
            throw new IllegalArgumentException("ESS 정보가 존재하지 않습니다.");
        }

        return EssInfoDto.from(memberEss);
    }

    @Transactional(readOnly = true)
    public StatusInfoDto getRackStatusInfo(Integer essId) {

        RackStatusRecent rackStatusRecent = rackStatusRecentRepository.findByEssMaster_Id(essId);
        FireStatusRecent fireStatusRecent = fireStatusRecentRepository.findByEssId(essId);

        if  (rackStatusRecent == null) {
            throw new IllegalArgumentException("Rack 상태 정보가 존재하지 않습니다.");
        }

        return StatusInfoDto.from(rackStatusRecent, fireStatusRecent);
    }

    @Transactional(readOnly = true)
    public List<EventHistoryDto> getEventHistory(Integer essId) {
        List<EventHistory> eventHistoryList = eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId);
        return eventHistoryList.stream().map(EventHistoryDto::from).toList();
    }



}
