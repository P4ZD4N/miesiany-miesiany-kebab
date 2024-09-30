package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.exceptions.BeverageAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.BeverageNotFoundException;
import com.p4zd4n.kebab.exceptions.GlobalExceptionHandler;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.requests.menu.NewBeverageRequest;
import com.p4zd4n.kebab.requests.menu.RemovedBeverageRequest;
import com.p4zd4n.kebab.requests.menu.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.menu.BeverageResponse;
import com.p4zd4n.kebab.responses.menu.NewBeverageResponse;
import com.p4zd4n.kebab.responses.menu.RemovedBeverageResponse;
import com.p4zd4n.kebab.responses.menu.UpdatedBeverageResponse;
import com.p4zd4n.kebab.services.menu.AddonService;
import com.p4zd4n.kebab.services.menu.BeverageService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
    private BeverageRepository beverageRepository;

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
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(5))
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
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(5))
                .build();

        when(beverageService.addBeverage(request)).thenThrow(new BeverageAlreadyExistsException());

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("beverage.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Beverage with the same name and capacity already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/menu/add-beverage")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Beverage with the same name and capacity already exists!")));

        verify(beverageService, times(1)).addBeverage(request);
    }

    @Test
    public void addBeverage_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewBeverageRequest request = NewBeverageRequest.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(5))
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
                .name("Coca-Cola")
                .oldCapacity(BigDecimal.valueOf(0.5))
                .newCapacity(BigDecimal.valueOf(0.33))
                .price(BigDecimal.valueOf(5))
                .build();

        UpdatedBeverageResponse expectedResponse = UpdatedBeverageResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated beverage with name 'Coca-Cola'")
                .build();

        when(beverageService.findBeverageByNameAndCapacity(request.name(), request.oldCapacity()))
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

        verify(beverageService, times(1)).findBeverageByNameAndCapacity(request.name(), request.oldCapacity());
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
                .name("Coca-Cola")
                .oldCapacity(BigDecimal.valueOf(0.5))
                .newCapacity(BigDecimal.valueOf(0.33))
                .price(BigDecimal.valueOf(5))
                .build();

        when(beverageService.findBeverageByNameAndCapacity(request.name(), request.oldCapacity()))
                .thenReturn(existingBeverage);
        when(beverageService.updateBeverage(existingBeverage, request)).thenThrow(new BeverageAlreadyExistsException());

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("beverage.alreadyExists", null, Locale.forLanguageTag("pl")))
                .thenReturn("Napoj z taka sama nazwa i pojemnoscia juz istnieje!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MenuController(beverageService, addonService, mealService))
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
                .name("Coca-Cola")
                .oldCapacity(BigDecimal.valueOf(0.5))
                .newCapacity(BigDecimal.valueOf(0.33))
                .price(BigDecimal.valueOf(5))
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
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed beverage with name 'Coca-Cola'")));

        verify(beverageService, times(1)).findBeverageByNameAndCapacity(request.name(), request.capacity());
        verify(beverageService, times(1)).removeBeverage(existingBeverage);
    }

    @Test
    public void removeBeverage_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        RemovedBeverageRequest request = RemovedBeverageRequest.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .build();

        mockMvc.perform(delete("/api/v1/menu/remove-beverage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeBeverage_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(delete("/api/v1/menu/remove-beverage")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }
}
