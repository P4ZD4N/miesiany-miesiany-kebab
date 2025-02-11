package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.notfound.BeveragePromotionNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.MealPromotionNotFoundException;
import com.p4zd4n.kebab.repositories.BeveragePromotionsRepository;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.NewBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.UpdatedBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.NewBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.UpdatedBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.BeveragePromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void addBeveragePromotion_ShouldAddBeverage_WhenValidRequestWithNullMapInRequest() {

        NewBeveragePromotionRequest request = NewBeveragePromotionRequest.builder()
                .description("-10%")
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        BeveragePromotion newBeveragePromotion = BeveragePromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();
        newBeveragePromotion.setId(1L);

        when(beveragePromotionsRepository.save(any(BeveragePromotion.class))).thenReturn(newBeveragePromotion);

        NewBeveragePromotionResponse response = beveragePromotionsService.addBeveragePromotion(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new beverage promotion with id '1'", response.message());

        verify(beveragePromotionsRepository, times(1)).save(any(BeveragePromotion.class));
    }

    @Test
    public void addBeveragePromotion_ShouldAddBeverage_WhenValidRequestWithNotNullMapInRequest() {

        NewBeveragePromotionRequest request = NewBeveragePromotionRequest.builder()
                .description("-10%")
                .discountPercentage(BigDecimal.valueOf(10))
                .beveragesWithCapacities(Map.of(
                        "Coca-Cola", List.of(BigDecimal.valueOf(0.5))
                ))
                .build();

        BeveragePromotion newBeveragePromotion = BeveragePromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();
        newBeveragePromotion.setId(1L);

        when(beveragePromotionsRepository.save(any(BeveragePromotion.class))).thenReturn(newBeveragePromotion);

        NewBeveragePromotionResponse response = beveragePromotionsService.addBeveragePromotion(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new beverage promotion with id '1'", response.message());

        verify(beveragePromotionsRepository, times(1)).save(any(BeveragePromotion.class));
    }


    @Test
    public void findBeveragePromotionById_ShouldReturnBeveragePromotion_IfExists() {

        BeveragePromotion beveragePromotion = BeveragePromotion.builder()
                .description("-20%")
                .discountPercentage(BigDecimal.valueOf(20))
                .build();

        when(beveragePromotionsRepository.findById(1L)).thenReturn(Optional.of(beveragePromotion));

        BeveragePromotion foundBeveragePromotion = beveragePromotionsService.findBeveragePromotionById(1L);

        assertNotNull(foundBeveragePromotion);
        assertEquals("-20%", foundBeveragePromotion.getDescription());
        assertEquals(BigDecimal.valueOf(20), foundBeveragePromotion.getDiscountPercentage());

        verify(beveragePromotionsRepository, times(1)).findById(1L);
    }

    @Test
    public void findBeveragePromotionById_ShouldThrowBeveragePromotionNotFoundException_IfDoesNotExists() {

        when(beveragePromotionsRepository.findById(100L)).thenThrow(new BeveragePromotionNotFoundException(100L));

        BeveragePromotionNotFoundException exception = assertThrows(BeveragePromotionNotFoundException.class, () -> {
            beveragePromotionsService.findBeveragePromotionById(100L);
        });

        assertEquals("Beverage promotion with id '100' not found!", exception.getMessage());

        verify(beveragePromotionsRepository, times(1)).findById(100L);
    }

    @Test
    public void updateBeveragePromotion_ShouldUpdateBeveragePromotion_WhenValidRequest() {

        BeveragePromotion beveragePromotion = BeveragePromotion.builder()
                .description("-20%")
                .discountPercentage(BigDecimal.valueOf(20))
                .build();
        beveragePromotion.setId(1L);

        UpdatedBeveragePromotionRequest request = UpdatedBeveragePromotionRequest.builder()
                .id(1L)
                .updatedDescription("Siema")
                .build();

        when(beveragePromotionsRepository.save(any(BeveragePromotion.class))).thenReturn(beveragePromotion);

        UpdatedBeveragePromotionResponse response = beveragePromotionsService.updateBeveragePromotion(beveragePromotion, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully updated beverage promotion with id '1'", response.message());
        assertEquals("Siema", beveragePromotion.getDescription());

        verify(beveragePromotionsRepository, times(1)).save(beveragePromotion);
    }
}
