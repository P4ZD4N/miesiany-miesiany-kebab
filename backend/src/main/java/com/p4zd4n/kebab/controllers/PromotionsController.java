package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.AddonPromotion;
import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.NewAddonPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.RemovedAddonPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.UpdatedAddonPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.NewBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.RemovedBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.UpdatedBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.RemovedMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.AddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.NewAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.RemovedAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.UpdatedAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.NewBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.RemovedBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.UpdatedBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.RemovedMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.AddonPromotionsService;
import com.p4zd4n.kebab.services.promotions.BeveragePromotionsService;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.mail.MessagingException;
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
    private final AddonPromotionsService addonPromotionsService;

    public PromotionsController(
            MealPromotionsService mealPromotionsService,
            BeveragePromotionsService beveragePromotionsService,
            AddonPromotionsService addonPromotionsService
    ) {
        this.mealPromotionsService = mealPromotionsService;
        this.beveragePromotionsService = beveragePromotionsService;
        this.addonPromotionsService = addonPromotionsService;
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
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

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
        LanguageValidator.validateLanguage(language);

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

    @PostMapping("/add-beverage-promotion")
    public ResponseEntity<NewBeveragePromotionResponse> addBeveragePromotion(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewBeveragePromotionRequest request
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

        log.info("Received add beverage promotion request");

        NewBeveragePromotionResponse response = beveragePromotionsService.addBeveragePromotion(request);

        log.info("Successfully added new beverage promotion");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-beverage-promotion")
    public ResponseEntity<UpdatedBeveragePromotionResponse> updateBeveragePromotion(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedBeveragePromotionRequest request
    ) {
        LanguageValidator.validateLanguage(language);

        log.info("Received update beverage promotion request");

        BeveragePromotion existingBeveragePromotion = beveragePromotionsService.findBeveragePromotionById(request.id());
        UpdatedBeveragePromotionResponse response = beveragePromotionsService.updateBeveragePromotion(
                existingBeveragePromotion, request);

        log.info("Successfully updated beverage promotion with id  '{}'", existingBeveragePromotion.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-beverage-promotion")
    public ResponseEntity<RemovedBeveragePromotionResponse> removeBeveragePromotion(
            @Valid @RequestBody RemovedBeveragePromotionRequest request
    ) {
        log.info("Received remove beverage promotion request");

        BeveragePromotion existingBeveragePromotion = beveragePromotionsService.findBeveragePromotionById(request.id());
        RemovedBeveragePromotionResponse response = beveragePromotionsService.removeBeveragePromotion(existingBeveragePromotion);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/addon-promotions")
    public ResponseEntity<List<AddonPromotionResponse>> getAddonPromotions() {
        log.info("Received get addon promotions request");
        return ResponseEntity.ok(addonPromotionsService.getAddonPromotions());
    }

    @PostMapping("/add-addon-promotion")
    public ResponseEntity<NewAddonPromotionResponse> addAddonPromotion(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewAddonPromotionRequest request
    ) throws MessagingException {
        LanguageValidator.validateLanguage(language);

        log.info("Received add addon promotion request");

        NewAddonPromotionResponse response = addonPromotionsService.addAddonPromotion(request);

        log.info("Successfully added new addon promotion");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-addon-promotion")
    public ResponseEntity<UpdatedAddonPromotionResponse> updateAddonPromotion(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedAddonPromotionRequest request
    ) {
        LanguageValidator.validateLanguage(language);

        log.info("Received update addon promotion request");

        AddonPromotion existingAddonPromotion = addonPromotionsService.findAddonPromotionById(request.id());
        UpdatedAddonPromotionResponse response = addonPromotionsService.updateAddonPromotion(existingAddonPromotion, request);

        log.info("Successfully updated addon promotion with id  '{}'", existingAddonPromotion.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-addon-promotion")
    public ResponseEntity<RemovedAddonPromotionResponse> removeAddonPromotion(
            @Valid @RequestBody RemovedAddonPromotionRequest request
    ) {
        log.info("Received remove addon promotion request");

        AddonPromotion existingAddonPromotion = addonPromotionsService.findAddonPromotionById(request.id());
        RemovedAddonPromotionResponse response = addonPromotionsService.removeAddonPromotion(existingAddonPromotion);

        return ResponseEntity.ok(response);
    }
}
