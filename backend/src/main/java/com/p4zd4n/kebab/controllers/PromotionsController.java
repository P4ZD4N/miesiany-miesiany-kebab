package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.RemovedMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.RemovedMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.BeveragePromotionsService;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class PromotionsController {

    private final MealPromotionsService mealPromotionsService;
    private final BeveragePromotionsService beveragePromotionsService;

    public PromotionsController(MealPromotionsService mealPromotionsService, BeveragePromotionsService beveragePromotionsService) {
        this.mealPromotionsService = mealPromotionsService;
        this.beveragePromotionsService = beveragePromotionsService;
    }

    @GetMapping("/meal-promotions")
    public ResponseEntity<List<MealPromotionResponse>> getMealPromotions() {
        log.info("Received get meal promotions request");
        return ResponseEntity.ok(mealPromotionsService.getMealPromotions());
    }

    @PostMapping("/add-meal-promotion")
    public ResponseEntity<NewMealPromotionResponse> addMealPromotion(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewMealPromotionRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add meal promotion request");

        NewMealPromotionResponse response = mealPromotionsService.addMealPromotion(request);

        log.info("Successfully added new meal promotion");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-meal-promotion")
    public ResponseEntity<UpdatedMealPromotionResponse> updateMealPromotion(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedMealPromotionRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received update meal promotion request");

        MealPromotion existingMealPromotion = mealPromotionsService.findMealPromotionById(request.id());
        UpdatedMealPromotionResponse response = mealPromotionsService.updateMealPromotion(existingMealPromotion, request);

        log.info("Successfully updated meal promotion with id  '{}'", existingMealPromotion.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-meal-promotion")
    public ResponseEntity<RemovedMealPromotionResponse> removeMealPromotion(
            @Valid @RequestBody RemovedMealPromotionRequest request
    ) {
        log.info("Received remove meal promotion request");

        MealPromotion existingMealPromotion = mealPromotionsService.findMealPromotionById(request.id());
        RemovedMealPromotionResponse response = mealPromotionsService.removeMealPromotion(existingMealPromotion);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/beverage-promotions")
    public ResponseEntity<List<BeveragePromotionResponse>> getBeveragePromotions() {
        log.info("Received get beverage promotions request");
        return ResponseEntity.ok(beveragePromotionsService.getBeveragePromotions());
    }
}
