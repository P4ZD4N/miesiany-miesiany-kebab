package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
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

    public PromotionsController(MealPromotionsService mealPromotionsService) {
        this.mealPromotionsService = mealPromotionsService;
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
}
