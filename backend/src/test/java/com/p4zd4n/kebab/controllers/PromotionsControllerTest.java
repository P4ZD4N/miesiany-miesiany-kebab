package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.menu.beverages.RemovedBeverageRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.RemovedMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.menu.beverages.RemovedBeverageResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.RemovedMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PromotionsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PromotionsControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MealPromotionsRepository mealPromotionsRepository;

    @MockBean
    private MealRepository mealRepository;

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

    @Test
    public void addMealPromotion_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewMealPromotionRequest request = NewMealPromotionRequest.builder()
                .newMealPromotionDescription("-10%")
                .newMealPromotionSizes(Set.of(Size.SMALL))
                .newMealPromotionDiscountPercentage(BigDecimal.valueOf(10))
                .newMealPromotionMealNames(List.of("Pita"))
                .build();

        NewMealPromotionResponse response = NewMealPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new meal promotion with id '1'")
                .build();

        when(mealPromotionsService.addMealPromotion(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/promotions/add-meal-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new meal promotion with id '1'")));
    }

    @Test
    public void addMealPromotion_ShouldReturnBadRequest_WhenInvalidDescription() throws Exception {

        NewMealPromotionRequest request = NewMealPromotionRequest.builder()
                .newMealPromotionDescription("")
                .newMealPromotionSizes(Set.of(Size.SMALL))
                .newMealPromotionDiscountPercentage(BigDecimal.valueOf(10))
                .newMealPromotionMealNames(List.of("Pita"))
                .build();

        mockMvc.perform(post("/api/v1/promotions/add-meal-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addMealPromotion_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewMealPromotionRequest request = NewMealPromotionRequest.builder()
                .newMealPromotionDescription("-10%")
                .newMealPromotionSizes(Set.of(Size.SMALL))
                .newMealPromotionDiscountPercentage(BigDecimal.valueOf(10))
                .newMealPromotionMealNames(List.of("Pita"))
                .build();

        mockMvc.perform(post("/api/v1/promotions/add-meal-promotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addMealPromotion_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/promotions/add-meal-promotion")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void updateMealPromotion_ShouldReturnOk_WhenValidRequest() throws Exception {

        MealPromotion mealPromotion = MealPromotion.builder()
                .description("Large -20%")
                .sizes(Set.of(Size.LARGE))
                .discountPercentage(BigDecimal.valueOf(20))
                .build();
        mealPromotion.setId(1L);

        UpdatedMealPromotionRequest request = UpdatedMealPromotionRequest.builder()
                .updatedMealPromotionId(1L)
                .updatedMealPromotionDescription("Siema")
                .build();

        UpdatedMealPromotionResponse response = UpdatedMealPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated meal promotion with id '1'")
                .build();

        when(mealPromotionsService.findMealPromotionById(1L)).thenReturn(mealPromotion);
        when(mealPromotionsService.updateMealPromotion(mealPromotion, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/promotions/update-meal-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated meal promotion with id '1'")));

        verify(mealPromotionsService, times(1)).findMealPromotionById(request.updatedMealPromotionId());
        verify(mealPromotionsService, times(1)).updateMealPromotion(mealPromotion, request);
    }

    @Test
    public void updateMealPromotion_ShouldReturnBadRequest_WhenInvalidDescription() throws Exception {

        UpdatedMealPromotionRequest request = UpdatedMealPromotionRequest.builder()
                .updatedMealPromotionDescription("")
                .build();

        mockMvc.perform(put("/api/v1/promotions/update-meal-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMealPromotion_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        UpdatedMealPromotionRequest request = UpdatedMealPromotionRequest.builder()
                .updatedMealPromotionDescription("-10%")
                .build();

        mockMvc.perform(put("/api/v1/promotions/update-meal-promotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMealPromotion_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/promotions/update-meal-promotion")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeMealPromotion_ShouldReturnOk_WhenValidRequest() throws Exception {

        MealPromotion mealPromotion = MealPromotion.builder()
                .description("Large -20%")
                .sizes(Set.of(Size.LARGE))
                .discountPercentage(BigDecimal.valueOf(20))
                .build();
        mealPromotion.setId(1L);

        RemovedMealPromotionRequest request = RemovedMealPromotionRequest.builder()
                .id(1L)
                .build();

        RemovedMealPromotionResponse response = RemovedMealPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed meal promotion with id '1'")
                .build();

        when(mealPromotionsService.findMealPromotionById(request.id())).thenReturn(mealPromotion);
        when(mealPromotionsService.removeMealPromotion(any(MealPromotion.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/promotions/remove-meal-promotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed meal promotion with id '1'")));

        verify(mealPromotionsService, times(1)).findMealPromotionById(request.id());
        verify(mealPromotionsService, times(1)).removeMealPromotion(mealPromotion);
    }
}
