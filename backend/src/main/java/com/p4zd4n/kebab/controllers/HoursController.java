package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.requests.hour.UpdatedHourRequest;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.responses.hours.UpdatedHourResponse;
import com.p4zd4n.kebab.services.hours.HoursService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
      @Valid @RequestBody UpdatedHourRequest request) {
    LanguageValidator.validateLanguage(language);

    log.info("Received update opening hour request");

    OpeningHour existingHour = hoursService.findOpeningHourByDayOfWeek(request.dayOfWeek());
    UpdatedHourResponse response = hoursService.updateOpeningHour(existingHour, request);

    log.info("Successfully updated opening hour on {}", existingHour.getDayOfWeek());

    return ResponseEntity.ok(response);
  }
}
