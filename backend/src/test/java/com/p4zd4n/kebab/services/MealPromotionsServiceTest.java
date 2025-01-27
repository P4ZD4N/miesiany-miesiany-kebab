package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MealPromotionsServiceTest {

    @Mock
    private MealPromotionsRepository mealPromotionsRepository;

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
}
