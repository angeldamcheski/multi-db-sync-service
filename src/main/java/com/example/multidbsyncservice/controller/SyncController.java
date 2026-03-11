package com.example.multidbsyncservice.controller;

import com.example.multidbsyncservice.service.SyncEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {
    private final SyncEngineService syncEngineService;

    @PostMapping("/run/all")
    public ResponseEntity<String> triggerAll(){
        CompletableFuture.runAsync(syncEngineService::syncAllSources);
        return ResponseEntity.accepted().body("Synchronization started for all sources");
    }
}
