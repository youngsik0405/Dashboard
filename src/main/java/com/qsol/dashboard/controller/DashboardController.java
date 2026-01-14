package com.qsol.dashboard.controller;

import com.qsol.dashboard.dto.EventHistoryDto;
import com.qsol.dashboard.entity.EventHistory;
import com.qsol.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

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

        model.addAttribute("essInfo", dashboardService.getEssInfo(essId));
        model.addAttribute("rackStatusInfo", dashboardService.getRackStatusInfo(essId));
        model.addAttribute("moduleInfo", dashboardService.getModuleInfo(essId));
        model.addAttribute("eventHistory", dashboardService.getEventHistory(essId));

//        model.addAttribute("rackStatusInfo", rackStatusInfo);
//        model.addAttribute("eventHistory", dashboardService.getEventHistory(essId).stream().limit(3).toList());

        return "main";
    }
}
