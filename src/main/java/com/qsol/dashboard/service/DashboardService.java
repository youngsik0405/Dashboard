package com.qsol.dashboard.service;

import com.qsol.dashboard.dto.*;
import com.qsol.dashboard.entity.*;
import com.qsol.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final EssRackStatusMinuteRepository essRackStatusMinuteRepository;
    private final EssWarningFaultDetailRepository essWarningFaultDetailRepository;

    private final EssRackStatusHistoryRepository essRackStatusHistoryRepository;


    // 대시보드에 필요한 모든 데이터
    public Map<String, Object> getDashboardData(Integer essId) {

        EssInfoDto essInfo = getEssInfo(essId);
        RackStatusDto rackStatus = getRackStatusInfo(essId);
        FireStatusDto fireStatus = getFireStatusInfo(essId);
        List<EssModuleStatusDto> moduleInfo = getModuleInfo(essId);
        List<EventHistoryDto> eventHistory = getEventHistory(essId);

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

    // RackStatus 정보
    public RackStatusDto getRackStatusInfo(Integer essId) {
        try {
            RackStatusRecent rackStatusRecent = rackStatusRecentRepository.findByEssId(essId);
            return rackStatusRecent == null ? null : RackStatusDto.from(rackStatusRecent);
        } catch (Exception e) {
            log.error("RackStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    // 화재 감지 정보
    public FireStatusDto getFireStatusInfo(Integer essId) {
        try {
            FireStatusRecent fireStatusRecent = fireStatusRecentRepository.findByEssId(essId);
            return fireStatusRecent == null ? null : FireStatusDto.from(fireStatusRecent);
        } catch (Exception e) {
            log.error("FireStatusInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }


    // EventHistory 정보
    public List<EventHistoryDto> getEventHistory(Integer essId) {
        try {
            List<EventHistory> eventList = eventHistoryRepository.findTop9ByEssIdOrderByEventDtDesc(essId);

            List<EventHistoryDto> eventHistoryList = eventList.stream().map(EventHistoryDto::from).toList();
            return eventHistoryList.isEmpty() ? null : eventHistoryList;
        } catch (Exception e) {
            log.error("EventHistory 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    // EventHistory 상세 정보
    public EssWarningFaultDetailDto getEventDetail(Integer eventId) {
            long t0 = System.currentTimeMillis();
        try {
            long t1 = System.currentTimeMillis();
            EssWarningFaultDetail essWarningFaultDetail = essWarningFaultDetailRepository.findByEventId(eventId);
            long t2 = System.currentTimeMillis();

            log.info("[eventDetail] eventId={} repo={}ms total={}ms", eventId, (t2 - t1), (t2 - t0));

            return essWarningFaultDetail == null ? null : EssWarningFaultDetailDto.from(essWarningFaultDetail);
        } catch (Exception e) {
            log.error("EventDetail 조회 실패 eventId={}", eventId, e);
            return null;
        }
    }

    // Module 정보
    public List<EssModuleStatusDto> getModuleInfo(Integer essId) {
        try {
            List<EssModuleStatusDto> essModuleStatusList = essModuleStatusRecentRepository.findByEssId(essId).stream().map(EssModuleStatusDto::from).toList();
            return essModuleStatusList.isEmpty() ? null : essModuleStatusList;
        } catch (Exception e) {
            log.error("ModuleInfo 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    // Cell 정보
    public List<EssCellStatusDto> getCellInfo(Integer essId, Integer moduleId) {
        try {
            List<EssCellStatusDto> essCellStatusList = essCellStatusRecentRepository.findByEssIdAndModuleIdOrderByCellIdAsc(essId, moduleId).stream().map(EssCellStatusDto::from).toList();
            return essCellStatusList.isEmpty() ? null : essCellStatusList;
        } catch (Exception e) {
            log.error("CellInfo 조회 실패 essId={}, moduleId={}", essId, moduleId, e);
            return null;
        }
    }

    // RackHistory 정보 (차트 그리기 위해서)
    public Map<String, Object> getEssRackStatusMinuteData(Integer essId, Integer rackDeviceId) {
        try {
            // 현재 시점을 기준으로 3시간 전
            LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
            // 3시간 전 ~ 현재까지의 데이터를 createdAt 오름차순으로 조회
            List<EssRackStatusMinuteDto> essRackStatusMinuteList = essRackStatusMinuteRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, threeHoursAgo).stream().map(EssRackStatusMinuteDto::from).toList();

            return convertToChartData(essRackStatusMinuteList, null);
        } catch (Exception e) {
            log.error("EssRackStatusMinuteData 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    // 최신 RackStatus (차트 업데이트 위해서)
    public Map<String, Object> getLatestRackStatus(Integer essId, Integer rackDeviceId, LocalDateTime lastCreatedAt) {
        try {
            // lastCreatedAt 이후의 최신 데이터를 createdAt 오름차순으로 조회
            List<EssRackStatusMinuteDto> essRackStatusMinuteList = essRackStatusMinuteRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, lastCreatedAt).stream().map(EssRackStatusMinuteDto::from).toList();

            return convertToChartData(essRackStatusMinuteList, lastCreatedAt);
        } catch (Exception e) {
            log.error("LatestRackStatus 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    // 차트 데이터 변환 메서드
    private Map<String, Object> convertToChartData(List<EssRackStatusMinuteDto> essRackStatusMinuteList, LocalDateTime lastCreatedAt){
        List<Long> xAxis = new ArrayList<>();                   // x축
        List<BigDecimal> voltageData = new ArrayList<>();       // 전압
        List<BigDecimal> currentData = new ArrayList<>();       // 전류
        List<BigDecimal> temperatureData = new ArrayList<>();   // 온도

        // 이전 시간
        LocalDateTime prevTime = lastCreatedAt;

        for (EssRackStatusMinuteDto dto : essRackStatusMinuteList) {
            // 현재 데이터 시간
            LocalDateTime currentTime = dto.getCreatedAt();

            // 이전 시간이 있을때
            if (prevTime != null) {
                // 데이터 간격 (이전 데이터와 현재 데이터 차이 계산)
                long diff = Duration.between(prevTime, currentTime).toMillis();

                // 3분이상 차이 통신 끊김으로 판단
                if (diff > 180000) {
                    // 그래프를 끊기 위해서 이전 시간 +1분에 null 삽입
                    xAxis.add(prevTime.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    voltageData.add(null);
                    currentData.add(null);
                    temperatureData.add(null);

                    // 그래프 재시작 -1분에 null 삽입
                    xAxis.add(currentTime.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    voltageData.add(null);
                    currentData.add(null);
                    temperatureData.add(null);
                }
            }

            // 정상 데이터 추가
            xAxis.add(currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            voltageData.add(dto.getRackDcVoltage());
            currentData.add(dto.getRackCurrent());
            temperatureData.add(dto.getRackTemperature());

            // 현재시간을 이전시간에 저장
            prevTime = currentTime;
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("xAxis", xAxis);
        chartData.put("voltageData", voltageData);
        chartData.put("currentData", currentData);
        chartData.put("temperatureData", temperatureData);

        return chartData;
    }
}
