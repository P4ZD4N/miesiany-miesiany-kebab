package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.exceptions.InvalidCapacityException;
import com.p4zd4n.kebab.exceptions.InvalidPriceException;
import com.p4zd4n.kebab.requests.menu.RemovedBeverageRequest;
import com.p4zd4n.kebab.requests.menu.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.menu.*;
import com.p4zd4n.kebab.services.menu.AddonService;
import com.p4zd4n.kebab.services.menu.BeverageService;
import com.p4zd4n.kebab.services.menu.MealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PutMapping("/update-beverage")
    public ResponseEntity<UpdatedBeverageResponse> updateBeverage(
            @RequestBody UpdatedBeverageRequest request
    ) {
        if (request.capacity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidCapacityException(request.capacity());
        }

        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPriceException(request.price());
        }

        log.info("Received update beverage request");

        Beverage existingBeverage = beverageService.findBeverageByName(request.name());
        UpdatedBeverageResponse response = beverageService.updateBeverage(existingBeverage, request);

        log.info("Successfully updated beverage: {}", existingBeverage.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-beverage")
    public ResponseEntity<RemovedBeverageResponse> removeBeverage(
            @RequestBody RemovedBeverageRequest request
    ) {
        log.info("Received remove beverage request");

        Beverage existingBeverage = beverageService.findBeverageByName(request.name());
        RemovedBeverageResponse response = beverageService.removeBeverage(existingBeverage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/addons")
    public ResponseEntity<List<AddonResponse>> getAddons() {
        log.info("Received get addons request");

        return ResponseEntity.ok(addonService.getAddons());
    }

    @GetMapping("/meals")
    public ResponseEntity<List<MealResponse>> getMeals() {
        log.info("Received get meals request");

        return ResponseEntity.ok(mealService.getMeals());
    }
}
