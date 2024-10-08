package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.hour.UpdatedHourRequest;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.responses.hours.UpdatedHourResponse;
import com.p4zd4n.kebab.services.hours.HoursService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UpdatedHourResponse> updateOpeningHour(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedHourRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received update opening hour request");

        OpeningHour existingHour = hoursService.findOpeningHourByDayOfWeek(request.dayOfWeek());
        UpdatedHourResponse response = hoursService.updateOpeningHour(existingHour, request);

        log.info("Successfully updated opening hour on {}", existingHour.getDayOfWeek());

        return ResponseEntity.ok(response);
    }
}
