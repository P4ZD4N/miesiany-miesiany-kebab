package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.responses.promotions.MealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromotionsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PromotionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealPromotionsService mealPromotionsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getMealPromotions_ShouldReturnMealPromotions_WhenCalled() throws Exception {

        List<MealPromotionResponse> mealPromotionsList = Arrays.asList(
                MealPromotionResponse.builder()
                        .description("Large -20%")
                        .sizes(Set.of(Size.LARGE))
                        .mealNames(Arrays.asList("meal1", "meal2", "meal3"))
                        .discountPercentage(BigDecimal.valueOf(20))
                        .build(),
                MealPromotionResponse.builder()
                        .description("Small and medium -10%")
                        .sizes(Set.of(Size.SMALL, Size.MEDIUM))
                        .mealNames(Arrays.asList("meal1", "meal2", "meal3"))
                        .discountPercentage(BigDecimal.valueOf(10))
                        .build()
        );

        when(mealPromotionsService.getMealPromotions()).thenReturn(mealPromotionsList);

        mockMvc.perform(get("/api/v1/promotions/meal-promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sizes", hasSize(1)))
                .andExpect(jsonPath("$[0].discount_percentage", is(20)))
                .andExpect(jsonPath("$[1].meal_names[0]", is("meal1")))
                .andExpect(jsonPath("$[1].description", is("Small and medium -10%")));

        verify(mealPromotionsService, times(1)).getMealPromotions();
    }
}
