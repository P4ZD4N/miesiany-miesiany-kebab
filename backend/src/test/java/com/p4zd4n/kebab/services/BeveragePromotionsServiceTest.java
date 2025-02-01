package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.BeveragePromotionsRepository;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.BeveragePromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BeveragePromotionsServiceTest {

    @Mock
    private BeveragePromotionsRepository beveragePromotionsRepository;

    @Mock
    private BeverageRepository beverageRepository;

    @InjectMocks
    private BeveragePromotionsService beveragePromotionsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getBeveragePromotions_ShouldReturnBeveragePromotions_WhenCalled() {

        List<BeveragePromotion> beveragePromotionsList = Arrays.asList(
                BeveragePromotion.builder()
                        .description("Coca-Cola -20%")
                        .discountPercentage(BigDecimal.valueOf(20))
                        .build(),
                BeveragePromotion.builder()
                        .description("Fanta -10%")
                        .discountPercentage(BigDecimal.valueOf(10))
                        .build()
        );

        when(beveragePromotionsRepository.findAll()).thenReturn(beveragePromotionsList);

        List<BeveragePromotionResponse> result = beveragePromotionsService.getBeveragePromotions();

        assertEquals(2, result.size());
        assertEquals("Coca-Cola -20%", result.getFirst().description());
        assertEquals(BigDecimal.valueOf(10), result.getLast().discountPercentage());

        verify(beveragePromotionsRepository, times(1)).findAll();
    }
}
