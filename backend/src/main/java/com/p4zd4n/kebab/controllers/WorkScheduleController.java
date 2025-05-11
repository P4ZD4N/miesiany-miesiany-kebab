package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.workschedule.NewWorkScheduleEntryRequest;
import com.p4zd4n.kebab.responses.workschedule.NewWorkScheduleEntryResponse;
import com.p4zd4n.kebab.responses.workschedule.WorkScheduleEntryResponse;
import com.p4zd4n.kebab.services.workschedule.WorkScheduleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/add-entry")
    public ResponseEntity<NewWorkScheduleEntryResponse> addWorkScheduleEntry(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewWorkScheduleEntryRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add work schedule entry request");

        NewWorkScheduleEntryResponse response = workScheduleService.addWorkScheduleEntry(request);

        log.info("Successfully added new work schedule entry");

        return ResponseEntity.ok(response);
    }
}
