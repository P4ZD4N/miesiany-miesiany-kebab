package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.requests.hour.UpdatedHourRequest;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.services.hours.HoursService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hours")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class HoursController {

    private final HoursService hoursService;

    public HoursController(HoursService hoursService) {
        this.hoursService = hoursService;
    }

    @GetMapping("/opening-hours")
    public ResponseEntity<List<OpeningHoursResponse>> getOpeningHours() {
        log.info("Received get opening hours request");

        return ResponseEntity.ok(hoursService.getOpeningHours());
    }

    @PutMapping("/update-opening-hour")
    public ResponseEntity<OpeningHour> updateOpeningHour(
            @RequestBody UpdatedHourRequest request
    ) {
        log.info("Received update opening hour request");

        OpeningHour existingHour = hoursService.findOpeningHourByDayOfWeek(request.dayOfWeek());

        existingHour.setOpeningTime(request.openingTime());
        existingHour.setClosingTime(request.closingTime());

        OpeningHour savedHour = hoursService.saveOpeningHour(existingHour);

        return ResponseEntity.ok(savedHour);
    }
}
