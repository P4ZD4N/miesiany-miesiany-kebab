package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MealPromotionsServiceTest {

    @Mock
    private MealPromotionsRepository mealPromotionsRepository;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealPromotionsService mealPromotionsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMealPromotions_ShouldReturnMealPromotions_WhenCalled() {

        List<MealPromotion> mealPromotionsList = Arrays.asList(
                MealPromotion.builder()
                        .description("Large -20%")
                        .sizes(Set.of(Size.LARGE))
                        .discountPercentage(BigDecimal.valueOf(20))
                        .build(),
                MealPromotion.builder()
                        .description("Small and medium -10%")
                        .sizes(Set.of(Size.SMALL, Size.MEDIUM))
                        .discountPercentage(BigDecimal.valueOf(10))
                        .build()
        );

        when(mealPromotionsRepository.findAll()).thenReturn(mealPromotionsList);

        List<MealPromotionResponse> result = mealPromotionsService.getMealPromotions();

        assertEquals(2, result.size());

        assertEquals("Large -20%", result.getFirst().description());
        assertEquals(BigDecimal.valueOf(10), result.getLast().discountPercentage());

        verify(mealPromotionsRepository, times(1)).findAll();
    }

    @Test
    public void addMealPromotion_ShouldAddMeal_WhenValidRequest() {

        NewMealPromotionRequest request = NewMealPromotionRequest.builder()
                .newMealPromotionDescription("-10%")
                .newMealPromotionSizes(Set.of(Size.SMALL))
                .newMealPromotionDiscountPercentage(BigDecimal.valueOf(10))
                .newMealPromotionMealNames(List.of("Pita"))
                .build();

        MealPromotion newMealPromotion = MealPromotion.builder()
                .description(request.newMealPromotionDescription())
                .sizes(request.newMealPromotionSizes())
                .discountPercentage(request.newMealPromotionDiscountPercentage())
                .build();
        newMealPromotion.setId(1L);

        when(mealPromotionsRepository.save(any(MealPromotion.class))).thenReturn(newMealPromotion);

        NewMealPromotionResponse response = mealPromotionsService.addMealPromotion(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new meal promotion with id '1'", response.message());

        verify(mealPromotionsRepository, times(1)).save(any(MealPromotion.class));
    }
}
