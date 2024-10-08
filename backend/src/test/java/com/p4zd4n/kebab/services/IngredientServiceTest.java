package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.enums.IngredientType;
import com.p4zd4n.kebab.exceptions.alreadyexists.IngredientAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.IngredientNotFoundException;
import com.p4zd4n.kebab.repositories.IngredientRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.menu.ingredients.NewIngredientRequest;
import com.p4zd4n.kebab.responses.menu.ingredients.IngredientResponse;
import com.p4zd4n.kebab.responses.menu.ingredients.NewIngredientResponse;
import com.p4zd4n.kebab.responses.menu.ingredients.RemovedIngredientResponse;
import com.p4zd4n.kebab.services.menu.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getIngredients_ShouldReturnIngredients_WhenCalled() {

        List<Ingredient> ingredients = List.of(
                Ingredient.builder()
                        .name("Jalapeno")
                        .ingredientType(IngredientType.VEGETABLE)
                        .build(),
                Ingredient.builder()
                        .name("Tortilla")
                        .ingredientType(IngredientType.BREAD)
                        .build()
        );

        when(ingredientRepository.findAll()).thenReturn(ingredients);

        List<IngredientResponse> result = ingredientService.getIngredients();

        assertEquals(2, result.size());

        assertEquals("Jalapeno", result.getFirst().name());
        assertEquals(IngredientType.VEGETABLE, result.getFirst().ingredientType());

        assertEquals("Tortilla", result.getLast().name());
        assertEquals(IngredientType.BREAD, result.getLast().ingredientType());

        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    public void findIngredientByName_ShouldReturnIngredient_WhenIngredientExists() {

        Ingredient ingredient = Ingredient.builder()
                .name("Tortilla")
                .ingredientType(IngredientType.BREAD)
                .build();

        when(ingredientRepository.findByName("Tortilla")).thenReturn(Optional.of(ingredient));

        Ingredient foundIngredient = ingredientService.findIngredientByName("Tortilla");

        assertNotNull(foundIngredient);
        assertEquals("Tortilla", foundIngredient.getName());
        assertEquals(IngredientType.BREAD, foundIngredient.getIngredientType());

        verify(ingredientRepository, times(1)).findByName("Tortilla");
    }

    @Test
    public void findIngredientByName_ShouldThrowIngredientNotFoundException_WhenIngredientDoesNotExist() {

        when(ingredientRepository.findByName("Tortilla")).thenThrow(new IngredientNotFoundException("Tortilla"));

        IngredientNotFoundException exception = assertThrows(IngredientNotFoundException.class, () -> {
            ingredientService.findIngredientByName("Tortilla");
        });

        assertEquals("Ingredient with name 'Tortilla' not found!", exception.getMessage());

        verify(ingredientRepository, times(1)).findByName("Tortilla");
    }

    @Test
    public void addIngredient_ShouldAddIngredient_WhenIngredientDoesNotExist() {

        NewIngredientRequest request = NewIngredientRequest.builder()
                .newIngredientName("Chicken")
                .newIngredientType(IngredientType.MEAT)
                .build();

        Ingredient newIngredient = Ingredient.builder()
                .name(request.newIngredientName())
                .ingredientType(request.newIngredientType())
                .build();

        when(ingredientRepository.findByName(request.newIngredientName())).thenReturn(Optional.empty());
        when(ingredientRepository.save(any(Ingredient.class))).thenReturn(newIngredient);

        NewIngredientResponse response = ingredientService.addIngredient(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new ingredient with name 'Chicken'", response.message());

        verify(ingredientRepository, times(1)).findByName(request.newIngredientName());
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    public void addIngredient_ShouldThrowIngredientAlreadyExistsException_WhenIngredientExists() {

        NewIngredientRequest request = NewIngredientRequest.builder()
                .newIngredientName("Chicken")
                .newIngredientType(IngredientType.MEAT)
                .build();

        Ingredient existingIngredient = Ingredient.builder()
                .name("Chicken")
                .ingredientType(IngredientType.MEAT)
                .build();

        when(ingredientRepository.findByName(request.newIngredientName())).thenReturn(Optional.of(existingIngredient));

        assertThrows(IngredientAlreadyExistsException.class, () -> {
            ingredientService.addIngredient(request);
        });

        verify(ingredientRepository, times(1)).findByName(request.newIngredientName());
    }

    @Test
    public void removeIngredient_ShouldRemoveIngredient_WhenCalled() {

        Ingredient ingredient = Ingredient.builder()
                .name("Chicken")
                .ingredientType(IngredientType.MEAT)
                .build();

        doNothing().when(ingredientRepository).delete(ingredient);

        RemovedIngredientResponse response = ingredientService.removeIngredient(ingredient);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully removed ingredient with name 'Chicken'", response.message());

        verify(ingredientRepository, times(1)).delete(ingredient);
    }
}
