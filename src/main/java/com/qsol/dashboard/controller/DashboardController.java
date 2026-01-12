package com.qsol.dashboard.controller;

import com.qsol.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String main(@RequestParam(required = false) Integer essId, Model model) {
//        var rackStatusInfo = dashboardService.getRackStatusInfo(essId);
//        rackStatusInfo.setHasAlarm(true);
//        model.addAttribute("rackStatusInfo", rackStatusInfo);
        model.addAttribute("essInfo", dashboardService.getEssInfo(essId));
        model.addAttribute("rackStatusInfo", dashboardService.getRackStatusInfo(essId));

        return "main";
    }
}
