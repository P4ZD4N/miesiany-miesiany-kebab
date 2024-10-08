package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.exceptions.alreadyexists.IngredientAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.IngredientNotFoundException;
import com.p4zd4n.kebab.repositories.IngredientRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.menu.ingredients.NewIngredientRequest;
import com.p4zd4n.kebab.responses.menu.ingredients.IngredientResponse;
import com.p4zd4n.kebab.responses.menu.ingredients.NewIngredientResponse;
import com.p4zd4n.kebab.responses.menu.ingredients.RemovedIngredientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final MealRepository mealRepository;

    public IngredientService(IngredientRepository ingredientRepository, MealRepository mealRepository) {
        this.ingredientRepository = ingredientRepository;
        this.mealRepository = mealRepository;
    }

    public List<IngredientResponse> getIngredients() {

        log.info("Started retrieving ingredients");

        List<Ingredient> ingredients = ingredientRepository.findAll();

        List<IngredientResponse> response = ingredients.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved ingredients");

        return response;
    }

    private IngredientResponse mapToResponse(Ingredient ingredient) {

        return IngredientResponse.builder()
                .name(ingredient.getName())
                .ingredientType(ingredient.getIngredientType())
                .build();
    }

    public NewIngredientResponse addIngredient(NewIngredientRequest request) {

        Optional<Ingredient> ingredient = ingredientRepository.findByName(request.newIngredientName());

        if (ingredient.isPresent()) {
            throw new IngredientAlreadyExistsException(request.newIngredientName());
        }

        Ingredient newIngredient = Ingredient.builder()
                .name(request.newIngredientName())
                .ingredientType(request.newIngredientType())
                .build();
        Ingredient savedIngredient = ingredientRepository.save(newIngredient);
        NewIngredientResponse response = NewIngredientResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new ingredient with name '" + savedIngredient.getName() + "'")
                .build();

        log.info("Successfully added new ingredient with name '{}'", savedIngredient.getName());

        return response;
    }

    public Ingredient findIngredientByName(String name) {

        log.info("Started finding ingredient with name '{}'", name);

        Ingredient ingredient = ingredientRepository.findByName(name)
                .orElseThrow(() -> new IngredientNotFoundException(name));

        log.info("Successfully found ingredient with name '{}'", name);

        return ingredient;
    }

    public RemovedIngredientResponse removeIngredient(Ingredient ingredient) {
        log.info("Started removing ingredient with name '{}'", ingredient.getName());

        List<Meal> allMeals = mealRepository.findAll();

        allMeals.stream()
                .filter(meal -> meal.getMealIngredients().stream()
                        .anyMatch(ing -> ing.getIngredient().equals(ingredient)))
                .forEach(meal -> {
                    meal.removeIngredient(ingredient);
                });

        ingredientRepository.delete(ingredient);

        RemovedIngredientResponse response = RemovedIngredientResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed ingredient with name '" + ingredient.getName() + "'")
                .build();

        log.info("Successfully removed ingredient with name '{}'", ingredient.getName());

        return response;
    }
}
