package com.example.multidbsyncservice.controller;

import com.example.multidbsyncservice.entity.SynchronizedData;
import com.example.multidbsyncservice.repository.SynchronizedDataRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    private SynchronizedDataRepository synchronizedDataRepository;

    public DashboardController(SynchronizedDataRepository synchronizedDataRepository) {
        this.synchronizedDataRepository = synchronizedDataRepository;
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model){
        List<SynchronizedData> recentSyncs = synchronizedDataRepository.findRecentSyncs();
        model.addAttribute("records", recentSyncs);
        return "dashboard";
    }
}
