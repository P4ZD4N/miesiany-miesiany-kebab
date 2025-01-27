package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.notfound.MealPromotionNotFoundException;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    public MealPromotion findMealPromotionById(Long id) {

        log.info("Started finding meal promotion with id '{}'", id);

        MealPromotion mealPromotion = mealPromotionsRepository.findById(id)
                .orElseThrow(() -> new MealPromotionNotFoundException(id));

        log.info("Successfully found meal promotion with id '{}'", id);

        return mealPromotion;
    }

    public UpdatedMealPromotionResponse updateMealPromotion(MealPromotion mealPromotion, UpdatedMealPromotionRequest request) {

        UpdatedMealPromotionResponse response = UpdatedMealPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated meal promotion with id '" + mealPromotion.getId() + "'")
                .build();

        if (request.updatedMealPromotionDescription() != null)
            mealPromotion.setDescription(request.updatedMealPromotionDescription());

        if (request.updatedMealPromotionSizes() != null)
            mealPromotion.setSizes(request.updatedMealPromotionSizes());

        if (request.updatedMealPromotionDiscountPercentage() != null)
            mealPromotion.setDiscountPercentage(request.updatedMealPromotionDiscountPercentage());

        if (request.updatedMealPromotionMealNames() != null) {
            List<Meal> updatedMeals = mealRepository.findAll().stream()
                    .filter(meal -> request.updatedMealPromotionMealNames().contains(meal.getName()))
                    .toList();
            List<Meal> existingMeals = new ArrayList<>(mealPromotion.getMeals());

            existingMeals.forEach(meal -> {
                meal.getPromotions().remove(mealPromotion);
                mealRepository.save(meal);
            });

            updatedMeals.forEach(meal -> {
                meal.getPromotions().add(mealPromotion);
                mealRepository.save(meal);
            });

            mealPromotion.getMeals().clear();
            mealPromotion.getMeals().addAll(updatedMeals);
        }

        mealPromotionsRepository.save(mealPromotion);

        return response;
    }
}
