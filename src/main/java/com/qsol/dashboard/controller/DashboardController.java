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
import org.springframework.web.bind.annotation.ResponseBody;

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

        Map<String, Integer> sizeMap = new HashMap<>();
        sizeMap.put("eventHistorySize", dashboardData.get("eventHistory") == null ? 0 : ((List<?>) dashboardData.get("eventHistory")).size());
        sizeMap.put("moduleSize", dashboardData.get("moduleInfo") == null ? 0 : ((List<?>) dashboardData.get("moduleInfo")).size());
        sizeMap.put("cellSize", 0);


        model.addAttribute("essInfo", dashboardData.get("essInfo"));
        model.addAttribute("rackStatusInfo", dashboardData.get("rackStatusInfo"));
        model.addAttribute("fireStatusInfo", dashboardData.get("fireStatusInfo"));
        model.addAttribute("moduleInfo", dashboardData.get("moduleInfo"));
        model.addAttribute("eventHistory", dashboardData.get("eventHistory"));
        model.addAttribute("sizeMap", sizeMap);


        return "main";
    }

    @GetMapping("/api/updateDashboard")
    @ResponseBody
    public Map<String, Object> getUpdateDashboard(@RequestParam Integer essId){
        Map<String, Object> dashboardData = dashboardService.getDashboardData(essId);

        dashboardService.testData(dashboardData);

        Map<String, Integer> sizeMap = new HashMap<>();

        sizeMap.put("eventHistorySize", dashboardData.get("eventHistory") == null ? 0 : ((List<?>) dashboardData.get("eventHistory")).size());
        sizeMap.put("moduleSize", dashboardData.get("moduleInfo") == null ? 0 : ((List<?>) dashboardData.get("moduleInfo")).size());
        sizeMap.put("cellSize", 0);

        dashboardData.put("sizeMap", sizeMap);

        return dashboardData;
    }

    @GetMapping("/api/cellModal")
    @ResponseBody
    public Map<String, Object> getCellModal(@RequestParam Integer essId, @RequestParam Integer moduleId) {
        // 데이터 조회
        List<EssCellStatusDto> cellInfo = dashboardService.getCellInfo(essId, moduleId);

        // 테스트 용
        if (cellInfo != null) {
            for (EssCellStatusDto cell : cellInfo) {
                double randomVolt = 3.2 + (Math.random() * 0.4);
                cell.setVoltage(BigDecimal.valueOf(Math.round(randomVolt * 100) / 100.0));
            }
        }

        Map<String, Integer> sizeMap = new HashMap<>();
        sizeMap.put("cellSize", cellInfo == null ? 0 : cellInfo.size());

        Map<String, Object> cellInfoData = new HashMap<>();
        cellInfoData.put("cellInfo", cellInfo);
        cellInfoData.put("sizeMap", sizeMap);

        return cellInfoData;
    }
}
