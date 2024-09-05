package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.services.hours.HoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        return ResponseEntity.ok(hoursService.getOpeningHours());
    }
}
