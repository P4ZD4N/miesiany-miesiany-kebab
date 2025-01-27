package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MealPromotionsService {

    private final MealPromotionsRepository promotionsRepository;
    private final MealRepository mealRepository;
    private final MealPromotionsRepository mealPromotionsRepository;

    public MealPromotionsService(MealPromotionsRepository promotionsRepository, MealRepository mealRepository, MealPromotionsRepository mealPromotionsRepository) {
        this.promotionsRepository = promotionsRepository;
        this.mealRepository = mealRepository;
        this.mealPromotionsRepository = mealPromotionsRepository;
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

    public MealPromotionResponse mapToResponse(MealPromotion mealPromotion) {

        List<String> mealNames = mealPromotion.getMeals().stream()
                .map(Meal::getName)
                .toList();

        return MealPromotionResponse.builder()
                .description(mealPromotion.getDescription())
                .sizes(mealPromotion.getSizes())
                .discountPercentage(mealPromotion.getDiscountPercentage())
                .mealNames(mealNames)
                .build();
    }

    public NewMealPromotionResponse addMealPromotion(NewMealPromotionRequest request) {

        MealPromotion mealPromotion = MealPromotion.builder()
                .description(request.newMealPromotionDescription())
                .sizes(request.newMealPromotionSizes())
                .discountPercentage(request.newMealPromotionDiscountPercentage())
                .build();

        MealPromotion savedMealPromotion = mealPromotionsRepository.save(mealPromotion);

        mealRepository.findAll().stream()
                .filter(meal -> request.newMealPromotionMealNames().contains(meal.getName()))
                .forEach(meal -> {
                    meal.getPromotions().add(mealPromotion);
                    mealRepository.save(meal);
                });

        return NewMealPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new meal promotion with id '" + savedMealPromotion.getId() + "'")
                .build();
    }
}
