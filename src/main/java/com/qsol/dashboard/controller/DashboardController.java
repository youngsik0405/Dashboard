package com.qsol.dashboard.controller;

import com.qsol.dashboard.dto.EssCellStatusDto;
import com.qsol.dashboard.dto.EventHistoryDto;
import com.qsol.dashboard.entity.EventHistory;
import com.qsol.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String main(@RequestParam(required = false) Integer essId, Model model) {
//        var rackStatusInfo = dashboardService.getRackStatusInfo(essId);
//        rackStatusInfo.setHasAlarm(true);

        if (essId == null) {
            essId = 7;
        }

        Map<String, Object> dashboardData = dashboardService.getDashboardData(essId);

        model.addAttribute("essInfo", dashboardData.get("essInfo"));
        model.addAttribute("rackStatusInfo", dashboardData.get("rackStatusInfo"));
        model.addAttribute("fireStatusInfo", dashboardData.get("fireStatusInfo"));
        model.addAttribute("moduleInfo", dashboardData.get("moduleInfo"));
        model.addAttribute("eventHistory", dashboardData.get("eventHistory"));

//        model.addAttribute("rackStatusInfo", rackStatusInfo);

        return "main";
    }

    @GetMapping("/api/cellModal")
    public String getCellModal(@RequestParam Integer essId, @RequestParam Integer moduleId, Model model) {
        model.addAttribute("cellInfo", dashboardService.getCellInfo(essId, moduleId));
        return "main :: cellModalContent";
    }
}
