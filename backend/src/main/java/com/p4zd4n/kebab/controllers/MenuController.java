package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.responses.menu.AddonResponse;
import com.p4zd4n.kebab.responses.menu.BeverageResponse;
import com.p4zd4n.kebab.services.menu.AddonService;
import com.p4zd4n.kebab.services.menu.BeverageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class MenuController {

    private final BeverageService beverageService;
    private final AddonService addonService;

    public MenuController(BeverageService beverageService, AddonService addonService) {
        this.beverageService = beverageService;
        this.addonService = addonService;
    }

    @GetMapping("/beverages")
    public ResponseEntity<List<BeverageResponse>> getBeverages() {
        log.info("Received get beverages request");

        return ResponseEntity.ok(beverageService.getBeverages());
    }

    @GetMapping("/addons")
    public ResponseEntity<List<AddonResponse>> getAddons() {
        log.info("Received get addons request");

        return ResponseEntity.ok(addonService.getAddons());
    }
}
