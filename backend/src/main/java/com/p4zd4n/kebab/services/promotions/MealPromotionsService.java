package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.responses.promotions.MealPromotionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MealPromotionsService {

    private final MealPromotionsRepository promotionsRepository;

    public MealPromotionsService(MealPromotionsRepository promotionsRepository) {
        this.promotionsRepository = promotionsRepository;
    }

    public List<MealPromotionResponse> getMealPromotions() {

        log.info("Started retrieving meal promotions");

        List<MealPromotion> promotions = promotionsRepository.findAll();

        List<MealPromotionResponse> response = promotions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved meal promotions");

        return response;
    }

    private MealPromotionResponse mapToResponse(MealPromotion mealPromotion) {

        List<String> mealNames = mealPromotion.getMeals().stream()
                .map(Meal::getName)
                .toList();

        return MealPromotionResponse.builder()
                .description(mealPromotion.getDescription())
                .size(mealPromotion.getSize())
                .discountPercentage(mealPromotion.getDiscountPercentage())
                .mealNames(mealNames)
                .build();
    }
}
