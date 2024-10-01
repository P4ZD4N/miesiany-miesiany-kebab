package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.repositories.IngredientRepository;
import com.p4zd4n.kebab.responses.menu.IngredientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
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
}
