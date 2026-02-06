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

    // x축 리스트 만들어서
    // 각각의 항목에 대한 y축 값 리스트 생성
    // 묶음으로 만들어서 map에 담기
    // map return


    // RackHistory 정보 (차트 그리기 위해서)
    public Map<String, Object> getEssRackStatusMinuteData(Integer essId, Integer rackDeviceId) {
        try {
            // 현재 시점을 기준으로 1시간 전
            LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
            List<EssRackStatusMinuteDto> essRackStatusMinuteList = essRackStatusMinuteRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, threeHoursAgo).stream().map(EssRackStatusMinuteDto::from).toList();

            List<Long> xAxis = new ArrayList<>();
            List<BigDecimal> volatageData = new ArrayList<>();
            List<BigDecimal> currentData = new ArrayList<>();
            List<BigDecimal> temperatureData = new ArrayList<>();

            if (!essRackStatusMinuteList.isEmpty()) {
                LocalDateTime prevTime = null;

                for (EssRackStatusMinuteDto dto : essRackStatusMinuteList) {
                    LocalDateTime currentTime = dto.getCreatedAt();

                    if (prevTime != null) {
                        long diff = Duration.between(prevTime, currentTime).toMillis();

                        if (diff > 180000) {
                            xAxis.add(prevTime.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                            volatageData.add(null);
                            currentData.add(null);
                            temperatureData.add(null);

                            xAxis.add(currentTime.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                            volatageData.add(null);
                            currentData.add(null);
                            temperatureData.add(null);
                        }
                    }

                    xAxis.add(currentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                    volatageData.add(dto.getRackDcVoltage());
                    currentData.add(dto.getRackCurrent());
                    temperatureData.add(dto.getRackTemperature());

                    prevTime = currentTime;
                }
            }

            Map<String, Object> chartData = new HashMap<>();
            chartData.put("xAxis", xAxis);
            chartData.put("voltageData", volatageData);
            chartData.put("currentData", currentData);
            chartData.put("temperatureData", temperatureData);

            return chartData;
        } catch (Exception e) {
            log.error("EssRackStatusMinuteData 조회 실패 essId={}", essId, e);
            return null;
        }
    }

    // 최신 RackStatus (차트 업데이트 위해서)
    public List<EssRackStatusMinuteDto> getLatestRackStatus(Integer essId,Integer rackDeviceId, LocalDateTime lastCreatedAt) {
        try {
          List<EssRackStatusMinuteDto> essRackStatusMinuteList = essRackStatusMinuteRepository.findByEssIdAndRackDeviceIdAndCreatedAtAfterOrderByCreatedAtAsc(essId, rackDeviceId, lastCreatedAt).stream().map(EssRackStatusMinuteDto::from).toList();

          List<Object[]> voltageUpdates = new ArrayList<>();
          List<Object[]> currentUpdates = new ArrayList<>();
          List<Object[]> temperatureUpdates = new ArrayList<>();

          LocalDateTime prevTime = lastCreatedAt;
          long lastMillis = lastCreatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

          for (EssRackStatusMinuteDto dto : essRackStatusMinuteList) {
              LocalDateTime currentTime = dto.getCreatedAt();


              long diff = Duration.between(prevTime, currentTime).toMillis();

              if (diff > 180000) {
                  long gapStart = prevTime.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                  voltageUpdates.add(new Object[]{gapStart, null});
                  currentUpdates.add(new Object[]{gapStart, null});
                  temperatureUpdates.add(new Object[]{gapStart, null});

                  long gapEnd = currentTime.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                  voltageUpdates.add(new Object[]{gapEnd, null});
                  currentUpdates.add(new Object[]{gapEnd, null});
                  temperatureUpdates.add(new Object[]{gapEnd, null});
              }
          }


          return essRackStatusMinuteList.isEmpty() ? null : essRackStatusMinuteList;
        } catch (Exception e) {
            log.error("LatestRackStatus 조회 실패 essId={}", essId, e);
            return null;
        }
    }
}
