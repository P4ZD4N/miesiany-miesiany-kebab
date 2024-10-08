package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.enums.IngredientType;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.alreadyexists.MealAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.IngredientNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.MealNotFoundException;
import com.p4zd4n.kebab.repositories.IngredientRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.menu.meals.NewMealRequest;
import com.p4zd4n.kebab.requests.menu.meals.UpdatedMealRequest;
import com.p4zd4n.kebab.responses.menu.meals.*;
import com.p4zd4n.kebab.services.menu.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private MealService mealService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMeals_ShouldReturnMeals_WhenCalled() {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<Meal> meals = List.of(
                Meal.builder()
                    .name("Kebab1")
                    .prices(kebabPrices)
                    .build(),
                Meal.builder()
                        .name("Kebab2")
                        .prices(kebabPrices)
                        .build()
        );

        when(mealRepository.findAll()).thenReturn(meals);

        List<MealResponse> result = mealService.getMeals();

        assertEquals(2, result.size());

        assertEquals("Kebab1", result.getFirst().name());
        assertEquals(1, result.getFirst().prices().size());

        assertEquals("Kebab2", result.getLast().name());
        assertEquals(BigDecimal.valueOf(39), result.getLast().prices().get(Size.XL));

        verify(mealRepository, times(1)).findAll();
    }

    @Test
    public void findMealByName_ShouldReturnMeal_WhenMealExists() {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        Meal meal = Meal.builder()
            .name("Kebab")
            .prices(kebabPrices)
            .build();

        when(mealRepository.findByName("Kebab")).thenReturn(Optional.of(meal));

        Meal foundMeal = mealService.findMealByName("Kebab");

        assertNotNull(foundMeal);
        assertEquals("Kebab", foundMeal.getName());
        assertEquals(BigDecimal.valueOf(39), foundMeal.getPriceForSize(Size.XL));

        verify(mealRepository, times(1)).findByName("Kebab");
    }

    @Test
    public void findMealByName_ShouldThrowMealNotFoundException_WhenMealDoesNotExist() {

        when(mealRepository.findByName("Kebab")).thenThrow(new MealNotFoundException("Kebab"));

        MealNotFoundException exception = assertThrows(MealNotFoundException.class, () -> {
            mealService.findMealByName("Kebab");
        });

        assertEquals("Meal with name 'Kebab' not found!", exception.getMessage());

        verify(mealRepository, times(1)).findByName("Kebab");
    }

    @Test
    public void addMeal_ShouldAddMeal_WhenMealDoesNotExist() {

        List<SimpleMealIngredient> ingredients = List.of(
                SimpleMealIngredient.builder()
                        .name("Chicken")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        NewMealRequest request = NewMealRequest.builder()
                .newMealName("Kebab")
                .ingredients(ingredients)
                .build();

        when(mealRepository.findByName(request.newMealName())).thenReturn(Optional.empty());

        Meal newMeal = Meal.builder()
                .name(request.newMealName())
                .build();

        Ingredient chickenIngredient = Ingredient.builder()
                .name("Chicken")
                .ingredientType(IngredientType.MEAT)
                .build();

        when(mealRepository.save(any(Meal.class))).thenReturn(newMeal);
        when(ingredientRepository.findByName("Chicken")).thenReturn(Optional.of(chickenIngredient));

        NewMealResponse response = mealService.addMeal(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new meal with name 'Kebab'", response.message());

        verify(mealRepository, times(1)).findByName(request.newMealName());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    public void addMeal_ShouldThrowMealAlreadyExistsException_WhenMealExists() {

        NewMealRequest request = NewMealRequest.builder()
                .newMealName("Kebab")
                .build();

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .build();

        when(mealRepository.findByName(request.newMealName())).thenReturn(Optional.of(existingMeal));

        assertThrows(MealAlreadyExistsException.class, () -> {
            mealService.addMeal(request);
        });

        verify(mealRepository, times(1)).findByName(request.newMealName());
    }

    @Test
    public void addMeal_ShouldThrowIngredientNotFoundException_WhenIngredientDoesNotExist() {

        List<SimpleMealIngredient> ingredients = List.of(
                SimpleMealIngredient.builder()
                        .name("Chicken")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        NewMealRequest request = NewMealRequest.builder()
                .newMealName("Kebab")
                .ingredients(ingredients)
                .build();

        when(ingredientRepository.findByName("Chicken")).thenReturn(Optional.empty());

        assertThrows(IngredientNotFoundException.class, () -> {
            mealService.addMeal(request);
        });

        verify(mealRepository, times(1)).findByName(request.newMealName());
    }

    @Test
    public void updateMeal_ShouldUpdateMeal_WhenValidRequest() {

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .prices(new EnumMap<>(Size.class))
                .build();

        UpdatedMealRequest request = UpdatedMealRequest.builder()
                .updatedMealName("Kebab")
                .updatedPrices(new EnumMap<>(Size.class))
                .updatedIngredients(List.of(
                        SimpleMealIngredient.builder()
                                .name("Chicken")
                                .ingredientType(IngredientType.MEAT)
                                .build()
                ))
                .build();

        Ingredient chickenIngredient = Ingredient.builder()
                .name("Chicken")
                .ingredientType(IngredientType.MEAT)
                .build();

        when(ingredientRepository.findByName("Chicken")).thenReturn(Optional.of(chickenIngredient));
        when(mealRepository.save(any(Meal.class))).thenReturn(existingMeal);

        UpdatedMealResponse response = mealService.updateMeal(existingMeal, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully updated meal with name 'Kebab'", response.message());
        assertEquals("Kebab", existingMeal.getName());
        assertEquals(request.updatedPrices(), existingMeal.getPrices());
        assertEquals(1, existingMeal.getMealIngredients().size());
        assertEquals(chickenIngredient, existingMeal.getMealIngredients().getFirst().getIngredient());
    }

    @Test
    public void updateMeal_ShouldThrowIngredientNotFoundException_WhenIngredientDoesNotExist() {

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .prices(new EnumMap<>(Size.class))
                .build();

        UpdatedMealRequest request = UpdatedMealRequest.builder()
                .updatedMealName("Kebab")
                .updatedPrices(new EnumMap<>(Size.class))
                .updatedIngredients(List.of(
                        SimpleMealIngredient.builder()
                                .name("Chicken")
                                .ingredientType(IngredientType.MEAT)
                                .build()
                ))
                .build();

        when(ingredientRepository.findByName("Chicken")).thenReturn(Optional.empty());

        assertThrows(IngredientNotFoundException.class, () -> {
            mealService.updateMeal(existingMeal, request);
        });
    }

    @Test
    public void removeMeal_ShouldRemoveMeal_WhenCalled() {

        Meal meal = Meal.builder()
                .name("Kebab")
                .prices(new EnumMap<>(Size.class))
                .build();

        doNothing().when(mealRepository).delete(meal);

        RemovedMealResponse response = mealService.removeMeal(meal);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully removed meal with name 'Kebab'", response.message());
    }
}
