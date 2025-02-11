package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.repositories.*;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.NewBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.RemovedBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.UpdatedBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.RemovedMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.AddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.NewBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.RemovedBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.UpdatedBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.RemovedMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.AddonPromotionsService;
import com.p4zd4n.kebab.services.promotions.BeveragePromotionsService;
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
import java.util.Map;
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
    private BeveragePromotionsRepository beveragePromotionsRepository;

    @MockBean
    private MealRepository mealRepository;

    @MockBean
    private BeverageRepository beverageRepository;

    @MockBean
    private AddonRepository addonRepository;

    @MockBean
    private MealPromotionsService mealPromotionsService;

    @MockBean
    private BeveragePromotionsService beveragePromotionsService;

    @MockBean
    private AddonPromotionsService addonPromotionsService;

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
                .description("-10%")
                .sizes(Set.of(Size.SMALL))
                .discountPercentage(BigDecimal.valueOf(10))
                .mealNames(List.of("Pita"))
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
                .description("")
                .sizes(Set.of(Size.SMALL))
                .discountPercentage(BigDecimal.valueOf(10))
                .mealNames(List.of("Pita"))
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
                .description("-10%")
                .sizes(Set.of(Size.SMALL))
                .discountPercentage(BigDecimal.valueOf(10))
                .mealNames(List.of("Pita"))
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
                .id(1L)
                .updatedDescription("Siema")
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

        verify(mealPromotionsService, times(1)).findMealPromotionById(request.id());
        verify(mealPromotionsService, times(1)).updateMealPromotion(mealPromotion, request);
    }

    @Test
    public void updateMealPromotion_ShouldReturnBadRequest_WhenInvalidDescription() throws Exception {

        UpdatedMealPromotionRequest request = UpdatedMealPromotionRequest.builder()
                .updatedDescription("")
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
                .updatedDescription("-10%")
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

    @Test
    public void getBeveragePromotions_ShouldReturnBeveragePromotions_WhenCalled() throws Exception {

        List<BeveragePromotionResponse> beveragePromotionsList = Arrays.asList(
                BeveragePromotionResponse.builder()
                        .description("Coca-Cola -20%")
                        .discountPercentage(BigDecimal.valueOf(20))
                        .beveragesWithCapacities(Map.of(
                                "Coca-Cola",
                                List.of(BigDecimal.valueOf(0.33))
                        ))
                        .build(),
                BeveragePromotionResponse.builder()
                        .description("Fanta -10%")
                        .discountPercentage(BigDecimal.valueOf(10))
                        .beveragesWithCapacities(Map.of(
                                "Fanta",
                                List.of(BigDecimal.valueOf(0.33))
                        ))
                        .build()
        );

        when(beveragePromotionsService.getBeveragePromotions()).thenReturn(beveragePromotionsList);

        mockMvc.perform(get("/api/v1/promotions/beverage-promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].beverages_with_capacities.Coca-Cola", hasSize(1)))
                .andExpect(jsonPath("$[0].discount_percentage", is(20)))
                .andExpect(jsonPath("$[1].description", is("Fanta -10%")));

        verify(beveragePromotionsService, times(1)).getBeveragePromotions();
    }

    @Test
    public void addBeveragePromotion_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewBeveragePromotionRequest request = NewBeveragePromotionRequest.builder()
                .description("-10%")
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        NewBeveragePromotionResponse response = NewBeveragePromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new beverage promotion with id '1'")
                .build();

        when(beveragePromotionsService.addBeveragePromotion(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/promotions/add-beverage-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new beverage promotion with id '1'")));
    }

    @Test
    public void addBeveragePromotion_ShouldReturnBadRequest_WhenInvalidDescription() throws Exception {

        NewBeveragePromotionRequest request = NewBeveragePromotionRequest.builder()
                .description("")
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        mockMvc.perform(post("/api/v1/promotions/add-beverage-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBeveragePromotion_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewBeveragePromotionRequest request = NewBeveragePromotionRequest.builder()
                .description("-10%")
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        mockMvc.perform(post("/api/v1/promotions/add-beverage-promotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBeveragePromotion_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/promotions/add-beverage-promotion")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void updateBeveragePromotion_ShouldReturnOk_WhenValidRequest() throws Exception {

        BeveragePromotion beveragePromotion = BeveragePromotion.builder()
                .description("-20%")
                .discountPercentage(BigDecimal.valueOf(20))
                .build();
        beveragePromotion.setId(1L);

        UpdatedBeveragePromotionRequest request = UpdatedBeveragePromotionRequest.builder()
                .id(1L)
                .updatedDescription("Siema")
                .build();

        UpdatedBeveragePromotionResponse response = UpdatedBeveragePromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated beverage promotion with id '1'")
                .build();

        when(beveragePromotionsService.findBeveragePromotionById(1L)).thenReturn(beveragePromotion);
        when(beveragePromotionsService.updateBeveragePromotion(beveragePromotion, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/promotions/update-beverage-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated beverage promotion with id '1'")));

        verify(beveragePromotionsService, times(1)).findBeveragePromotionById(request.id());
        verify(beveragePromotionsService, times(1)).updateBeveragePromotion(beveragePromotion, request);
    }

    @Test
    public void updateBeveragePromotion_ShouldReturnBadRequest_WhenInvalidDescription() throws Exception {

        UpdatedBeveragePromotionRequest request = UpdatedBeveragePromotionRequest.builder()
                .updatedDescription("")
                .build();

        mockMvc.perform(put("/api/v1/promotions/update-beverage-promotion")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBeveragePromotion_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        UpdatedBeveragePromotionRequest request = UpdatedBeveragePromotionRequest.builder()
                .updatedDescription("-10%")
                .build();

        mockMvc.perform(put("/api/v1/promotions/update-beverage-promotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBeveragePromotion_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/promotions/update-beverage-promotion")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeBeveragePromotion_ShouldReturnOk_WhenValidRequest() throws Exception {

        BeveragePromotion beveragePromotion = BeveragePromotion.builder()
                .description("Large -20%")
                .discountPercentage(BigDecimal.valueOf(20))
                .build();
        beveragePromotion.setId(2L);

        RemovedBeveragePromotionRequest request = RemovedBeveragePromotionRequest.builder()
                .id(2L)
                .build();

        RemovedBeveragePromotionResponse response = RemovedBeveragePromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed beverage promotion with id '2'")
                .build();

        when(beveragePromotionsService.findBeveragePromotionById(request.id())).thenReturn(beveragePromotion);
        when(beveragePromotionsService.removeBeveragePromotion(any(BeveragePromotion.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/promotions/remove-beverage-promotion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed beverage promotion with id '2'")));

        verify(beveragePromotionsService, times(1)).findBeveragePromotionById(request.id());
        verify(beveragePromotionsService, times(1)).removeBeveragePromotion(beveragePromotion);
    }

    @Test
    public void getAddonPromotions_ShouldReturnAddonPromotions_WhenCalled() throws Exception {

        List<AddonPromotionResponse> addonPromotionsList = Arrays.asList(
                AddonPromotionResponse.builder()
                        .description("-20%")
                        .discountPercentage(BigDecimal.valueOf(20))
                        .build(),
                AddonPromotionResponse.builder()
                        .description("-10%")
                        .discountPercentage(BigDecimal.valueOf(10))
                        .build()
        );

        when(addonPromotionsService.getAddonPromotions()).thenReturn(addonPromotionsList);

        mockMvc.perform(get("/api/v1/promotions/addon-promotions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].discount_percentage", is(20)))
                .andExpect(jsonPath("$[1].description", is("-10%")));

        verify(addonPromotionsService, times(1)).getAddonPromotions();
    }
}
