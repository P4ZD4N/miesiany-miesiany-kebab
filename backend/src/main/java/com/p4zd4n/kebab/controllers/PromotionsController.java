package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.promotions.MealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
