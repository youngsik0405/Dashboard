package com.qsol.dashboard.controller;

import com.qsol.dashboard.dto.*;
import com.qsol.dashboard.service.DashboardService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
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

        model.addAllAttributes(dashboardData);
        model.addAttribute("size", dashboardData.get("sizeMap"));

        return "main";
    }

    // 대시보드 업데이트
    @GetMapping("/api/updateDashboard")
    @ResponseBody
    public Map<String, Object> getUpdateDashboard(@RequestParam Integer essId){
        Map<String, Object> dashboardData = dashboardService.getDashboardData(essId);
        return dashboardData;
    }

    // 모달 api
    @GetMapping("/api/cellModal")
    @ResponseBody
    public Map<String, Object> getCellModal(@RequestParam Integer essId, @RequestParam Integer moduleId) {
        // 데이터 조회
        List<EssCellStatusDto> cellInfo = dashboardService.getCellInfo(essId, moduleId);

        Map<String, Object> cellInfoData = new HashMap<>();
        cellInfoData.put("cellInfo", cellInfo);
        cellInfoData.put("size", cellInfo == null ? 0 : cellInfo.size());

        return cellInfoData;
    }

    @GetMapping("/api/chart")
    @ResponseBody
    public Map<String, Object> getEssRackStatusHistoryData(@RequestParam Integer essId, @RequestParam Integer rackDeviceId) {
        return dashboardService.getEssRackStatusMinuteData(essId, rackDeviceId);
    }

    @GetMapping("/api/chart/latest")
    @ResponseBody
    public List<EssRackStatusMinuteDto> getLatestEssRackStatus(@RequestParam Integer essId,
                                                               @RequestParam Integer rackDeviceId,
                                                               @RequestParam(required = false)
                                                               @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastCreatedAt) {
        return dashboardService.getLatestRackStatus(essId, rackDeviceId, lastCreatedAt);
    }

    @GetMapping("/api/eventDetail")
    @ResponseBody
    public EssWarningFaultDetailDto getEventDetail(@RequestParam Integer eventId) {
        return dashboardService.getEventDetail(eventId);
    }

}
