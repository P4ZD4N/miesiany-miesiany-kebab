package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.exceptions.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.exceptions.InvalidCapacityException;
import com.p4zd4n.kebab.exceptions.InvalidPriceException;
import com.p4zd4n.kebab.requests.menu.NewAddonRequest;
import com.p4zd4n.kebab.requests.menu.NewBeverageRequest;
import com.p4zd4n.kebab.requests.menu.RemovedBeverageRequest;
import com.p4zd4n.kebab.requests.menu.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.menu.*;
import com.p4zd4n.kebab.services.menu.AddonService;
import com.p4zd4n.kebab.services.menu.BeverageService;
import com.p4zd4n.kebab.services.menu.MealService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class MenuController {

    private final BeverageService beverageService;
    private final AddonService addonService;
    private final MealService mealService;

    public MenuController(BeverageService beverageService, AddonService addonService, MealService mealService) {
        this.beverageService = beverageService;
        this.addonService = addonService;
        this.mealService = mealService;
    }

    @GetMapping("/beverages")
    public ResponseEntity<List<BeverageResponse>> getBeverages() {
        log.info("Received get beverages request");

        return ResponseEntity.ok(beverageService.getBeverages());
    }

    @PostMapping("/add-beverage")
    public ResponseEntity<NewBeverageResponse> addBeverage(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewBeverageRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add beverage request");

        NewBeverageResponse response = beverageService.addBeverage(request);

        log.info("Successfully added new beverage: {}", request.name());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-beverage")
    public ResponseEntity<UpdatedBeverageResponse> updateBeverage(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedBeverageRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received update beverage request");

        Beverage existingBeverage = beverageService.findBeverageByNameAndCapacity(request.name(), request.oldCapacity());
        UpdatedBeverageResponse response = beverageService.updateBeverage(existingBeverage, request);

        log.info("Successfully updated beverage: {}", existingBeverage.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-beverage")
    public ResponseEntity<RemovedBeverageResponse> removeBeverage(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody RemovedBeverageRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received remove beverage request");

        Beverage existingBeverage = beverageService.findBeverageByNameAndCapacity(request.name(), request.capacity());
        RemovedBeverageResponse response = beverageService.removeBeverage(existingBeverage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/addons")
    public ResponseEntity<List<AddonResponse>> getAddons() {
        log.info("Received get addons request");

        return ResponseEntity.ok(addonService.getAddons());
    }

    @PostMapping("/add-addon")
    public ResponseEntity<NewAddonResponse> addAddon(
            @Valid @RequestBody NewAddonRequest request
    ) {
        log.info("Received add addon request");

        NewAddonResponse response = addonService.addAddon(request);

        log.info("Successfully added new addon: {}", request.name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/meals")
    public ResponseEntity<List<MealResponse>> getMeals() {
        log.info("Received get meals request");

        return ResponseEntity.ok(mealService.getMeals());
    }
}
