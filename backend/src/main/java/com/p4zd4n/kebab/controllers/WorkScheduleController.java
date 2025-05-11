package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.workschedule.WorkScheduleEntryResponse;
import com.p4zd4n.kebab.services.workschedule.WorkScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-schedule")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @GetMapping("/all-entries")
    public ResponseEntity<List<WorkScheduleEntryResponse>> getWorkScheduleEntries() {
        log.info("Received get work schedule entries request");
        return ResponseEntity.ok(workScheduleService.getWorkScheduleEntries());
    }
}
