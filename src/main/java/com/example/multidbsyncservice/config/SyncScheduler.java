package com.example.multidbsyncservice.config;

import com.example.multidbsyncservice.service.SyncEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SyncScheduler {
    private final SyncEngineService syncEngineService;

    @Scheduled(fixedDelay = 3000)
    public void runSync(){
        syncEngineService.syncAllSources();
    }

    //Cron expression to sync sources every minute
//    @Scheduled(cron = "0 * * * * *")
//    public void runSync(){
//        syncEngineService.syncAllSources();
//    }
}
