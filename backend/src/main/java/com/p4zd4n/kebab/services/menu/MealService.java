package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.AddonAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.IngredientNotFoundException;
import com.p4zd4n.kebab.exceptions.MealAlreadyExistsException;
import com.p4zd4n.kebab.repositories.IngredientRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.menu.NewAddonRequest;
import com.p4zd4n.kebab.requests.menu.NewMealRequest;
import com.p4zd4n.kebab.responses.menu.MealResponse;
import com.p4zd4n.kebab.responses.menu.NewAddonResponse;
import com.p4zd4n.kebab.responses.menu.NewMealResponse;
import com.p4zd4n.kebab.responses.menu.SimpleMealIngredient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MealService {

    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;

    public MealService(MealRepository mealRepository, IngredientRepository ingredientRepository) {
        this.mealRepository = mealRepository;
        this.ingredientRepository = ingredientRepository;
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

    public NewMealResponse addMeal(NewMealRequest request) {

        Optional<Meal> meal = mealRepository.findByName(request.newMealName());

        if (meal.isPresent()) {
            throw new MealAlreadyExistsException(request.newMealName());
        }

        Meal newMeal = Meal.builder()
                .name(request.newMealName())
                .prices(request.prices())
                .build();

        request.ingredients().forEach(ingredient -> newMeal.addIngredient(
                ingredientRepository.findByName(ingredient.name()).orElseThrow(
                        () -> new IngredientNotFoundException(ingredient.name()))
        ));

        Meal savedMeal = mealRepository.save(newMeal);
        NewMealResponse response = NewMealResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new meal with name '" + savedMeal.getName() + "'")
                .build();

        log.info("Successfully added new meal with name '{}'", savedMeal.getName());

        return response;
    }

    public void save(Meal meal) {
        mealRepository.save(meal);
    }
}
