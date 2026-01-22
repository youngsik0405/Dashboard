package com.qsol.dashboard.controller;

import com.qsol.dashboard.dto.EssCellStatusDto;
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

        model.addAttribute("essInfo", dashboardData.get("essInfo"));
        model.addAttribute("rackStatusInfo", dashboardData.get("rackStatusInfo"));
        model.addAttribute("fireStatusInfo", dashboardData.get("fireStatusInfo"));
        model.addAttribute("moduleInfo", dashboardData.get("moduleInfo"));
        model.addAttribute("eventHistory", dashboardData.get("eventHistory"));
        model.addAttribute("size", dashboardData.get("sizeMap"));

        return "main";
    }

    // 대시보드 업데이트 api
    @GetMapping("/api/updateDashboard")
    @ResponseBody
    public Map<String, Object> getUpdateDashboard(@RequestParam Integer essId){
        Map<String, Object> dashboardData = dashboardService.getDashboardData(essId);
        dashboardService.testData(dashboardData);
        dashboardData.put("size", dashboardData.get("sizeMap"));
        return dashboardData;
    }

    // 모달 api
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

        Map<String, Object> cellInfoData = new HashMap<>();
        cellInfoData.put("cellInfo", cellInfo);
        cellInfoData.put("size", cellInfo == null ? 0 : cellInfo.size());

        return cellInfoData;
    }
}
