package com.qsol.dashboard.controller;

import com.qsol.dashboard.dto.EssCellStatusDto;
import com.qsol.dashboard.dto.EssModuleStatusDto;
import com.qsol.dashboard.dto.EventHistoryDto;
import com.qsol.dashboard.dto.RackStatusDto;
import com.qsol.dashboard.entity.EventHistory;
import com.qsol.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String main(@RequestParam(required = false) Integer essId, Model model) {

        if (essId == null) {
            essId = 7;
        }

        Map<String, Object> dashboardData = dashboardService.getDashboardData(essId);

        List<EssModuleStatusDto> moduleInfo = (List<EssModuleStatusDto>) dashboardData.get("moduleInfo");
        int moduleSize = (moduleInfo == null) ? 0 : moduleInfo.size();

        List<EventHistoryDto> eventHistory = (List<EventHistoryDto>) dashboardData.get("eventHistory");
        int eventHistorySize = (eventHistory == null) ? 0 : eventHistory.size();

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("list", eventHistory);
        eventMap.put("size", eventHistory.size());


        Map<String, Integer> sizeMap = new HashMap<>();
        sizeMap.put("event", 10);
        sizeMap.put("chargeState", 10);
        model.addAttribute("essInfo", dashboardData.get("essInfo"));
        model.addAttribute("rackStatusInfo", dashboardData.get("rackStatusInfo"));
        model.addAttribute("fireStatusInfo", dashboardData.get("fireStatusInfo"));
        model.addAttribute("moduleInfo", moduleInfo);
        model.addAttribute("moduleSize", moduleSize);
        model.addAttribute("eventHistory", eventHistory);
        model.addAttribute("eventHistorySize", eventHistorySize);
        model.addAttribute("sizeMap", sizeMap);
        model.addAttribute("cellSize", 0);

        return "main";
    }

    @GetMapping("/api/eventHistoryFragment")
    public String getEventHistoryFragment(@RequestParam Integer essId, Model model) {
        List<EventHistoryDto> eventHistory = dashboardService.getEventHistory(essId);
        int eventHistorySize = (eventHistory == null) ? 0 : eventHistory.size();

        model.addAttribute("eventHistory", eventHistory);
        model.addAttribute("eventHistorySize", eventHistorySize);
        return "main :: eventHistoryFragment";
    }

    @GetMapping("/api/statusFragment")
    public String getStatusFragment(@RequestParam Integer essId, Model model) {
        Map<String, Object> dashboardData = dashboardService.getDashboardData(essId);

        RackStatusDto rackStatusInfo = (RackStatusDto) dashboardData.get("rackStatusInfo");

        // (테스트용)
        if (rackStatusInfo != null) {

            // 충전량 랜덤 값 생성
            double randomSoc = 80 + (Math.random() * 10);
            rackStatusInfo.setRackSoc(BigDecimal.valueOf(Math.round(randomSoc * 10) / 10.0)); // 소수점 한자리

            // 전압 랜덤 값 생성
            double randomVolt = 700 + (Math.random() * 50);
            rackStatusInfo.setRackDcVoltage(BigDecimal.valueOf(Math.round(randomVolt * 10) / 10.0));
        }

        model.addAttribute("rackStatusInfo", rackStatusInfo);
        model.addAttribute("fireStatusInfo", dashboardData.get("fireStatusInfo"));

        return "main :: statusFragment";
    }

    @GetMapping("/api/moduleFragment")
    public String getModuleFragment(@RequestParam Integer essId, Model model) {
        List<EssModuleStatusDto> moduleInfo = dashboardService.getModuleInfo(essId);
        int moduleSize = (moduleInfo == null) ? 0 : moduleInfo.size();

        if (moduleInfo != null) {
            for (EssModuleStatusDto module : moduleInfo) {
                // 테스트용 랜덤 데이터 주입

                // 전압: 3.2V ~ 3.5V 사이 랜덤
                double randomVolt = 33.2 + (Math.random() * 0.3);
                module.setModuleDcVoltage(BigDecimal.valueOf(Math.round(randomVolt * 100) / 100.0));

                // 셀 전압: 3.2V ~ 3.4V 사이 랜덤
                module.setMaxCellVoltage(BigDecimal.valueOf(Math.round((33.3 + Math.random() * 0.1) * 100) / 100.0));
                module.setMinCellVoltage(BigDecimal.valueOf(Math.round((33.1 + Math.random() * 0.1) * 100) / 100.0));

                // 온도: 20도 ~ 30도 사이 랜덤
                double randomTemp = 20 + (Math.random() * 10);
                module.setAvgModuleTemperature(BigDecimal.valueOf(Math.round(randomTemp * 10) / 10.0));

                // 특정 모듈에만 강제로 알람 띄우기 (테스트)
                if (module.getModuleId() % 2 == 0) {
                    module.setHasAlarm(true);
                }
            }
        }
        model.addAttribute("moduleInfo", moduleInfo);
        model.addAttribute("moduleSize", moduleSize);

        model.addAttribute("essInfo", dashboardService.getEssInfo(essId));
//        model.addAttribute("moduleInfo", dashboardService.getModuleInfo(essId));
        return "main :: moduleFragment";
    }



    @GetMapping("/api/cellModal")
    public String getCellModal(@RequestParam Integer essId, @RequestParam Integer moduleId, Model model) {
        // 1. 실제 데이터 조회
        List<EssCellStatusDto> cellInfo = dashboardService.getCellInfo(essId, moduleId);

        // 2. ✅ 실시간 변화 테스트를 위한 랜덤 전압 주입 (3.20V ~ 3.60V)
        if (cellInfo != null) {
            for (EssCellStatusDto cell : cellInfo) {
                double randomVolt = 3.2 + (Math.random() * 0.4);
                cell.setVoltage(BigDecimal.valueOf(Math.round(randomVolt * 100) / 100.0));
            }
        }

        int cellSize = (cellInfo == null) ? 0 : cellInfo.size();

        model.addAttribute("cellInfo", cellInfo);
        model.addAttribute("cellSize", cellSize);

//        model.addAttribute("cellInfo", dashboardService.getCellInfo(essId, moduleId));
        return "main :: cellModalContent";
    }
}
