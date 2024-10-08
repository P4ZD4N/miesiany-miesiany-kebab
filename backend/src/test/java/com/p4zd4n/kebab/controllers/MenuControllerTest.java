package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.Ingredient;
import com.p4zd4n.kebab.entities.Meal;
import com.p4zd4n.kebab.enums.IngredientType;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.*;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.repositories.IngredientRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.menu.*;
import com.p4zd4n.kebab.responses.menu.*;
import com.p4zd4n.kebab.services.menu.AddonService;
import com.p4zd4n.kebab.services.menu.BeverageService;
import com.p4zd4n.kebab.services.menu.IngredientService;
import com.p4zd4n.kebab.services.menu.MealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MenuControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BeverageService beverageService;

    @MockBean
    private AddonService addonService;

    @MockBean
    private MealService mealService;

    @MockBean
    private IngredientService ingredientService;

    @MockBean
    private BeverageRepository beverageRepository;

    @MockBean
    private IngredientRepository ingredientRepository;

    @MockBean
    private MealRepository mealRepository;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getBeverages_ShouldReturnBeverages_WhenCalled() throws Exception {

        List<BeverageResponse> beveragesList = Arrays.asList(
                BeverageResponse.builder()
                        .name("Coca-Cola")
                        .capacity(BigDecimal.valueOf(0.5))
                        .price(BigDecimal.valueOf(5))
                        .build(),
                BeverageResponse.builder()
                        .name("Fanta")
                        .capacity(BigDecimal.valueOf(0.33))
                        .price(BigDecimal.valueOf(4))
                        .build(),
                BeverageResponse.builder()
                        .name("Sprite")
                        .capacity(BigDecimal.valueOf(1))
                        .price(BigDecimal.valueOf(8))
                        .build()
        );

        when(beverageService.getBeverages()).thenReturn(beveragesList);

        mockMvc.perform(get("/api/v1/menu/beverages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("Coca-Cola")))
                .andExpect(jsonPath("$[1].capacity", is(0.33)))
                .andExpect(jsonPath("$[2].price", is(8)));

        verify(beverageService, times(1)).getBeverages();
    }

    @Test
    public void addBeverage_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewBeverageRequest request = NewBeverageRequest.builder()
                .newBeverageName("Coca-Cola")
                .newBeverageCapacity(BigDecimal.valueOf(0.5))
                .newBeveragePrice(BigDecimal.valueOf(5))
                .build();

        NewBeverageResponse response = NewBeverageResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new beverage with name 'Coca-Cola'")
                .build();

        when(beverageService.addBeverage(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/menu/add-beverage")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new beverage with name 'Coca-Cola'")));
    }

    @Test
    public void addBeverage_ShouldReturnConflict_WhenBeverageAlreadyExists() throws Exception {

        NewBeverageRequest request = NewBeverageRequest.builder()
                .newBeverageName("Coca-Cola")
                .newBeverageCapacity(BigDecimal.valueOf(0.5))
                .newBeveragePrice(BigDecimal.valueOf(5))
                .build();

        when(beverageService.addBeverage(request)).thenThrow(new BeverageAlreadyExistsException());

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("beverage.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Beverage with the same name and capacity already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService, ingredientService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-beverage")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.itemType", is("beverage")))
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Beverage with the same name and capacity already exists!")));

        verify(beverageService, times(1)).addBeverage(request);
    }

    @Test
    public void addBeverage_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewBeverageRequest request = NewBeverageRequest.builder()
                .newBeverageName("Coca-Cola")
                .newBeverageCapacity(BigDecimal.valueOf(0.5))
                .newBeveragePrice(BigDecimal.valueOf(5))
                .build();

        mockMvc.perform(post("/api/v1/menu/add-beverage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addBeverage_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/menu/add-beverage")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void updateBeverage_ShouldReturnOk_WhenValidRequest() throws Exception {

        Beverage existingBeverage = Beverage.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(7))
                .build();

        UpdatedBeverageRequest request = UpdatedBeverageRequest.builder()
                .updatedBeverageName("Coca-Cola")
                .updatedBeverageOldCapacity(BigDecimal.valueOf(0.5))
                .updatedBeverageNewCapacity(BigDecimal.valueOf(0.33))
                .updatedBeveragePrice(BigDecimal.valueOf(5))
                .build();

        UpdatedBeverageResponse expectedResponse = UpdatedBeverageResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated beverage with name 'Coca-Cola'")
                .build();

        when(beverageService.findBeverageByNameAndCapacity(request.updatedBeverageName(), request.updatedBeverageOldCapacity()))
                .thenReturn(existingBeverage);
        when(beverageService.updateBeverage(any(Beverage.class), any(UpdatedBeverageRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/menu/update-beverage")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated beverage with name 'Coca-Cola'")));

        verify(beverageService, times(1)).findBeverageByNameAndCapacity(
                request.updatedBeverageName(), request.updatedBeverageOldCapacity());
        verify(beverageService, times(1)).updateBeverage(existingBeverage, request);
    }

    @Test
    public void updateBeverage_ShouldReturnConflict_WhenBeverageAlreadyExists() throws Exception {

        Beverage existingBeverage = Beverage.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(7))
                .build();

        UpdatedBeverageRequest request = UpdatedBeverageRequest.builder()
                .updatedBeverageName("Coca-Cola")
                .updatedBeverageOldCapacity(BigDecimal.valueOf(0.5))
                .updatedBeverageNewCapacity(BigDecimal.valueOf(0.33))
                .updatedBeveragePrice(BigDecimal.valueOf(5))
                .build();

        when(beverageService.findBeverageByNameAndCapacity(request.updatedBeverageName(), request.updatedBeverageOldCapacity()))
                .thenReturn(existingBeverage);
        when(beverageService.updateBeverage(existingBeverage, request)).thenThrow(new BeverageAlreadyExistsException());

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("beverage.alreadyExists", null, Locale.forLanguageTag("pl")))
                .thenReturn("Napoj z taka sama nazwa i pojemnoscia juz istnieje!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService, ingredientService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/menu/update-beverage")
                .header("Accept-Language", "pl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Napoj z taka sama nazwa i pojemnoscia juz istnieje!")));

        verify(beverageService, times(1)).updateBeverage(existingBeverage, request);
    }

    @Test
    public void updateBeverage_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        UpdatedBeverageRequest request = UpdatedBeverageRequest.builder()
                .updatedBeverageName("Coca-Cola")
                .updatedBeverageOldCapacity(BigDecimal.valueOf(0.5))
                .updatedBeverageNewCapacity(BigDecimal.valueOf(0.33))
                .updatedBeveragePrice(BigDecimal.valueOf(5))
                .build();

        mockMvc.perform(put("/api/v1/menu/update-beverage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateBeverage_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/menu/update-beverage")
                            .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeBeverage_ShouldReturnOk_WhenValidRequest() throws Exception {

        Beverage existingBeverage = Beverage.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(7))
                .build();

        RemovedBeverageRequest request = RemovedBeverageRequest.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .build();

        RemovedBeverageResponse response = RemovedBeverageResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed beverage with name 'Coca-Cola'")
                .build();

        when(beverageService.findBeverageByNameAndCapacity(request.name(), request.capacity()))
                .thenReturn(existingBeverage);
        when(beverageService.removeBeverage(any(Beverage.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/menu/remove-beverage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed beverage with name 'Coca-Cola'")));

        verify(beverageService, times(1)).findBeverageByNameAndCapacity(request.name(), request.capacity());
        verify(beverageService, times(1)).removeBeverage(existingBeverage);
    }

    @Test
    public void getAddons_ShouldReturnAddons_WhenCalled() throws Exception {

        List<AddonResponse> addonList = Arrays.asList(
                AddonResponse.builder()
                        .name("Jalapeno")
                        .price(BigDecimal.valueOf(3))
                        .build(),
                AddonResponse.builder()
                        .name("Herbs")
                        .price(BigDecimal.valueOf(2))
                        .build()
        );

        when(addonService.getAddons()).thenReturn(addonList);

        mockMvc.perform(get("/api/v1/menu/addons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Jalapeno")))
                .andExpect(jsonPath("$[1].price", is(2)));

        verify(addonService, times(1)).getAddons();
    }

    @Test
    public void addAddon_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewAddonRequest request = NewAddonRequest.builder()
                .newAddonName("Jalapeno")
                .newAddonPrice(BigDecimal.valueOf(3))
                .build();

        NewAddonResponse response = NewAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new addon with name 'Jalapeno'")
                .build();

        when(addonService.addAddon(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/menu/add-addon")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new addon with name 'Jalapeno'")));
    }

    @Test
    public void addAddon_ShouldReturnConflict_WhenAddonAlreadyExists() throws Exception {

        NewAddonRequest request = NewAddonRequest.builder()
                .newAddonName("Jalapeno")
                .newAddonPrice(BigDecimal.valueOf(3))
                .build();

        when(addonService.addAddon(request)).thenThrow(new AddonAlreadyExistsException(request.newAddonName()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("addon.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Addon with the same name already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService, ingredientService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-addon")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.itemType", is("addon")))
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Addon with the same name already exists!")));

        verify(addonService, times(1)).addAddon(request);
    }

    @Test
    public void addAddon_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewAddonRequest request = NewAddonRequest.builder()
                .newAddonName("Jalapeno")
                .newAddonPrice(BigDecimal.valueOf(3))
                .build();

        mockMvc.perform(post("/api/v1/menu/add-addon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addAddon_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/menu/add-addon")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void updateAddon_ShouldReturnOk_WhenValidRequest() throws Exception {

        Addon existingAddon = Addon.builder()
                .name("Jalapeno")
                .price(BigDecimal.valueOf(3))
                .build();

        UpdatedAddonRequest request = UpdatedAddonRequest.builder()
                .updatedAddonName("Jalapeno")
                .updatedAddonPrice(BigDecimal.valueOf(4))
                .build();

        UpdatedAddonResponse expectedResponse = UpdatedAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated addon with name 'Jalapeno'")
                .build();

        when(addonService.findAddonByName(request.updatedAddonName()))
                .thenReturn(existingAddon);
        when(addonService.updateAddon(any(Addon.class), any(UpdatedAddonRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/menu/update-addon")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated addon with name 'Jalapeno'")));

        verify(addonService, times(1)).findAddonByName(request.updatedAddonName());
        verify(addonService, times(1)).updateAddon(existingAddon, request);
    }

    @Test
    public void updateAddon_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        UpdatedAddonRequest request = UpdatedAddonRequest.builder()
                .updatedAddonName("Jalapeno")
                .updatedAddonPrice(BigDecimal.valueOf(5))
                .build();

        mockMvc.perform(put("/api/v1/menu/update-addon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateAddon_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/menu/update-addon")
                            .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeAddon_ShouldReturnOk_WhenValidRequest() throws Exception {

        Addon existingAddon = Addon.builder()
                .name("Jalapeno")
                .price(BigDecimal.valueOf(3))
                .build();

        RemovedAddonRequest request = RemovedAddonRequest.builder()
                .name("Jalapeno")
                .build();

        RemovedAddonResponse response = RemovedAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed addon with name 'Jalapeno'")
                .build();

        when(addonService.findAddonByName(request.name())).thenReturn(existingAddon);
        when(addonService.removeAddon(any(Addon.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/menu/remove-addon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed addon with name 'Jalapeno'")));

        verify(addonService, times(1)).findAddonByName(request.name());
        verify(addonService, times(1)).removeAddon(existingAddon);
    }

    @Test
    public void getMeals_ShouldReturnMeals_WhenCalled() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.SMALL, BigDecimal.valueOf(20));
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> ingredientResponses = List.of(
                SimpleMealIngredient.builder()
                        .name("Mutton")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        List<MealResponse> mealList = Collections.singletonList(
                MealResponse.builder()
                        .name("Kebab")
                        .prices(kebabPrices)
                        .ingredients(ingredientResponses)
                        .build()
        );

        when(mealService.getMeals()).thenReturn(mealList);

        mockMvc.perform(get("/api/v1/menu/meals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Kebab")))
                .andExpect(jsonPath("$[0].prices", aMapWithSize(2)))
                .andExpect(jsonPath("$[0].prices.SMALL", is(20)))
                .andExpect(jsonPath("$[0].prices.XL", is(39)));

        verify(mealService, times(1)).getMeals();
    }

    @Test
    public void addMeal_ShouldReturnOk_WhenValidRequest() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> ingredientResponses = List.of(
                SimpleMealIngredient.builder()
                        .name("Mutton")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        NewMealRequest request = NewMealRequest.builder()
                .newMealName("Kebab")
                .ingredients(ingredientResponses)
                .prices(kebabPrices)
                .build();

        NewMealResponse response = NewMealResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new meal with name 'Kebab'")
                .build();

        when(mealService.addMeal(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/menu/add-meal")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new meal with name 'Kebab'")));
    }

    @Test
    public void addMeal_ShouldReturnConflict_WhenMealAlreadyExists() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> ingredientResponses = List.of(
                SimpleMealIngredient.builder()
                        .name("Mutton")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        NewMealRequest request = NewMealRequest.builder()
                .newMealName("Kebab")
                .ingredients(ingredientResponses)
                .prices(kebabPrices)
                .build();

        when(mealService.addMeal(request)).thenThrow(new MealAlreadyExistsException(request.newMealName()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("meal.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Meal with the same name already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService, ingredientService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-meal")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.itemType", is("meal")))
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Meal with the same name already exists!")));

        verify(mealService, times(1)).addMeal(request);
    }

    @Test
    public void addMeal_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> ingredientResponses = List.of(
                SimpleMealIngredient.builder()
                        .name("Mutton")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        NewMealRequest request = NewMealRequest.builder()
                .newMealName("Kebab")
                .ingredients(ingredientResponses)
                .prices(kebabPrices)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-meal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addMeal_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/menu/add-meal")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void updateMeal_ShouldReturnOk_WhenValidRequest() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> updatedIngredients = List.of(
                SimpleMealIngredient.builder()
                        .name("Chicken")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .prices(kebabPrices)
                .build();

        existingMeal.addIngredient(Ingredient.builder()
                .name("Mutton")
                .ingredientType(IngredientType.MEAT)
                .build()
        );

        UpdatedMealRequest request = UpdatedMealRequest.builder()
                .updatedMealName("Kebab")
                .updatedPrices(kebabPrices)
                .updatedIngredients(updatedIngredients)
                .build();

        UpdatedMealResponse expectedResponse = UpdatedMealResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated meal with name 'Kebab'")
                .build();

        when(mealService.findMealByName(request.updatedMealName())).thenReturn(existingMeal);
        when(mealService.updateMeal(any(Meal.class), any(UpdatedMealRequest.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/menu/update-meal")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated meal with name 'Kebab'")));

        verify(mealService, times(1)).findMealByName(request.updatedMealName());
        verify(mealService, times(1)).updateMeal(existingMeal, request);
    }

    @Test
    public void updateMeal_ShouldReturnNotFound_WhenIngredientNotExist() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> updatedIngredients = List.of(
                SimpleMealIngredient.builder()
                        .name("Chicken")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .prices(kebabPrices)
                .build();

        existingMeal.addIngredient(Ingredient.builder()
                .name("Mutton")
                .ingredientType(IngredientType.MEAT)
                .build()
        );

        UpdatedMealRequest request = UpdatedMealRequest.builder()
                .updatedMealName("Kebab")
                .updatedPrices(kebabPrices)
                .updatedIngredients(updatedIngredients)
                .build();

        when(mealService.findMealByName("Kebab")).thenReturn(existingMeal);
        when(mealService.updateMeal(existingMeal, request)).thenThrow(new IngredientNotFoundException("Chicken"));

        mockMvc.perform(put("/api/v1/menu/update-meal")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(mealService, times(1)).updateMeal(existingMeal, request);
    }

    @Test
    public void updateMeal_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        EnumMap<Size, BigDecimal> kebabPrices = new EnumMap<>(Size.class);
        kebabPrices.put(Size.XL, BigDecimal.valueOf(39));

        List<SimpleMealIngredient> updatedIngredients = List.of(
                SimpleMealIngredient.builder()
                        .name("Chicken")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .prices(kebabPrices)
                .build();

        existingMeal.addIngredient(Ingredient.builder()
                .name("Mutton")
                .ingredientType(IngredientType.MEAT)
                .build()
        );

        UpdatedMealRequest request = UpdatedMealRequest.builder()
                .updatedMealName("Kebab")
                .updatedPrices(kebabPrices)
                .updatedIngredients(updatedIngredients)
                .build();

        mockMvc.perform(put("/api/v1/menu/update-meal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateMeal_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/menu/update-meal")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeMeal_ShouldReturnOk_WhenValidRequest() throws Exception {

        Meal existingMeal = Meal.builder()
                .name("Kebab")
                .prices(new EnumMap<>(Size.class))
                .build();

        existingMeal.addIngredient(Ingredient.builder()
                .name("Mutton")
                .ingredientType(IngredientType.MEAT)
                .build()
        );

        RemovedMealRequest request = RemovedMealRequest.builder()
                .name("Kebab")
                .build();

        RemovedMealResponse response = RemovedMealResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed meal with name 'Kebab'")
                .build();

        when(mealService.findMealByName(request.name())).thenReturn(existingMeal);
        when(mealService.removeMeal(any(Meal.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/menu/remove-meal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed meal with name 'Kebab'")));

        verify(mealService, times(1)).findMealByName(request.name());
        verify(mealService, times(1)).removeMeal(existingMeal);
    }

    @Test
    public void getIngredients_ShouldReturnIngredients_WhenCalled() throws Exception {

        List<IngredientResponse> ingredientsList = Arrays.asList(
                IngredientResponse.builder()
                        .name("Tortilla")
                        .ingredientType(IngredientType.BREAD)
                        .build(),
                IngredientResponse.builder()
                        .name("Chicken")
                        .ingredientType(IngredientType.MEAT)
                        .build()
        );

        when(ingredientService.getIngredients()).thenReturn(ingredientsList);

        mockMvc.perform(get("/api/v1/menu/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ingredient_type", is("BREAD")))
                .andExpect(jsonPath("$[1].name", is("Chicken")))
                .andExpect(jsonPath("$[1].ingredient_type", is("MEAT")));

        verify(ingredientService, times(1)).getIngredients();
    }

    @Test
    public void addIngredient_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewIngredientRequest request = NewIngredientRequest.builder()
                .newIngredientName("Cucumber")
                .newIngredientType(IngredientType.VEGETABLE)
                .build();

        NewIngredientResponse response = NewIngredientResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new ingredient with name 'Cucumber'")
                .build();

        when(ingredientService.addIngredient(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/menu/add-ingredient")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new ingredient with name 'Cucumber'")));
    }

    @Test
    public void addIngredient_ShouldReturnConflict_WhenIngredientAlreadyExists() throws Exception {

        NewIngredientRequest request = NewIngredientRequest.builder()
                .newIngredientName("Cucumber")
                .newIngredientType(IngredientType.VEGETABLE)
                .build();

        when(ingredientService.addIngredient(request)).thenThrow(new IngredientAlreadyExistsException(request.newIngredientName()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("ingredient.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Ingredient with the same name already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService, ingredientService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-ingredient")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.itemType", is("ingredient")))
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Ingredient with the same name already exists!")));

        verify(ingredientService, times(1)).addIngredient(request);
    }

    @Test
    public void addIngredient_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewIngredientRequest request = NewIngredientRequest.builder()
                .newIngredientName("Cucumber")
                .newIngredientType(IngredientType.VEGETABLE)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-ingredient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addIngredient_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/menu/add-ingredient")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeIngredient_ShouldReturnOk_WhenValidRequest() throws Exception {

        Ingredient existingIngredient = Ingredient.builder()
                .name("Jalapeno")
                .ingredientType(IngredientType.VEGETABLE)
                .build();

        RemovedIngredientRequest request = RemovedIngredientRequest.builder()
                .name("Jalapeno")
                .build();

        RemovedIngredientResponse response = RemovedIngredientResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed ingredient with name 'Jalapeno'")
                .build();

        when(ingredientService.findIngredientByName(request.name())).thenReturn(existingIngredient);
        when(ingredientService.removeIngredient(any(Ingredient.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/menu/remove-ingredient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed ingredient with name 'Jalapeno'")));

        verify(ingredientService, times(1)).findIngredientByName(request.name());
        verify(ingredientService, times(1)).removeIngredient(existingIngredient);
    }
}
