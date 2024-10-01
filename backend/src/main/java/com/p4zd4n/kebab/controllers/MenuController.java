package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.exceptions.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.menu.*;
import com.p4zd4n.kebab.responses.menu.*;
import com.p4zd4n.kebab.services.menu.AddonService;
import com.p4zd4n.kebab.services.menu.BeverageService;
import com.p4zd4n.kebab.services.menu.IngredientService;
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
    private final IngredientService ingredientService;

    public MenuController(BeverageService beverageService,
                          AddonService addonService,
                          MealService mealService,
                          IngredientService ingredientService
    ) {
        this.beverageService = beverageService;
        this.addonService = addonService;
        this.mealService = mealService;
        this.ingredientService = ingredientService;
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

        log.info("Successfully added new beverage: {}", request.newBeverageName());

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

        Beverage existingBeverage = beverageService.findBeverageByNameAndCapacity(
                request.updatedBeverageName(), request.updatedBeverageOldCapacity());
        UpdatedBeverageResponse response = beverageService.updateBeverage(existingBeverage, request);

        log.info("Successfully updated beverage: {}", existingBeverage.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-beverage")
    public ResponseEntity<RemovedBeverageResponse> removeBeverage(
            @Valid @RequestBody RemovedBeverageRequest request
    ) {
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
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewAddonRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add addon request");

        NewAddonResponse response = addonService.addAddon(request);

        log.info("Successfully added new addon: {}", request.newAddonName());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-addon")
    public ResponseEntity<UpdatedAddonResponse> updateAddon(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedAddonRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received update addon request");

        Addon existingAddon = addonService.findAddonByName(request.updatedAddonName());
        UpdatedAddonResponse response = addonService.updateAddon(existingAddon, request);

        log.info("Successfully updated addon: {}", existingAddon.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-addon")
    public ResponseEntity<RemovedAddonResponse> removeAddon(
            @Valid @RequestBody RemovedAddonRequest request
    ) {
        log.info("Received remove addon request");

        Addon existingAddon = addonService.findAddonByName(request.name());
        RemovedAddonResponse response = addonService.removeAddon(existingAddon);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/meals")
    public ResponseEntity<List<MealResponse>> getMeals() {
        log.info("Received get meals request");

        return ResponseEntity.ok(mealService.getMeals());
    }

    @PostMapping("/add-meal")
    public ResponseEntity<NewMealResponse> addMeal(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewMealRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add meal request");

        NewMealResponse response = mealService.addMeal(request);

        log.info("Successfully added new meal: {}", request.newMealName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-meal")
    public ResponseEntity<RemovedMealResponse> removeMeal(
            @Valid @RequestBody RemovedMealRequest request
    ) {
        log.info("Received remove meal request");

        Meal existingMeal = mealService.findMealByName(request.name());
        RemovedMealResponse response = mealService.removeMeal(existingMeal);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientResponse>> getIngredients() {
        log.info("Received get ingredients request");

        return ResponseEntity.ok(ingredientService.getIngredients());
    }
}
