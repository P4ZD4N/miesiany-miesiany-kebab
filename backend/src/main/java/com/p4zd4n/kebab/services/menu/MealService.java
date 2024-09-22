package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.responses.menu.MealResponse;
import com.p4zd4n.kebab.responses.menu.SimpleMealIngredient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<MealResponse> getMeals() {

        log.info("Started retrieving meals");

        List<Meal> meals = mealRepository.findAll();

        List<MealResponse> response = meals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved meals");

        return response;
    }

    private MealResponse mapToResponse(Meal meal) {

        EnumMap<Size, BigDecimal> prices = new EnumMap<>(meal.getPrices());

        List<SimpleMealIngredient> ingredientResponses = meal.getMealIngredients().stream()
                .map(mealIngredient -> SimpleMealIngredient.builder()
                        .id(mealIngredient.getId())
                        .name(mealIngredient.getIngredient().getName())
                        .ingredientType(mealIngredient.getIngredient().getIngredientType())
                        .build()
                )
                .toList();

        return MealResponse.builder()
                .name(meal.getName())
                .prices(prices)
                .ingredients(ingredientResponses)
                .build();
    }

    public void save(Meal meal) {
        mealRepository.save(meal);
    }
}