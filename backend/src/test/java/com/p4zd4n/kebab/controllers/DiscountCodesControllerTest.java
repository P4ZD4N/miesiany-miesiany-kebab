package com.p4zd4n.kebab.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.DiscountCode;
import com.p4zd4n.kebab.exceptions.GlobalExceptionHandler;
import com.p4zd4n.kebab.exceptions.alreadyexists.DiscountCodeAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.expired.DiscountCodeExpiredException;
import com.p4zd4n.kebab.exceptions.notfound.DiscountCodeNotFoundException;
import com.p4zd4n.kebab.requests.discountcodes.NewDiscountCodeRequest;
import com.p4zd4n.kebab.requests.discountcodes.RemovedDiscountCodeRequest;
import com.p4zd4n.kebab.requests.discountcodes.UpdatedDiscountCodeRequest;
import com.p4zd4n.kebab.responses.discountcodes.DiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.NewDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.RemovedDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.UpdatedDiscountCodeResponse;
import com.p4zd4n.kebab.services.discountcodes.DiscountCodesService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

@WebMvcTest(DiscountCodesController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DiscountCodesControllerTest {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @MockBean private DiscountCodesService discountCodesService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getDiscountCodes_ShouldReturnDiscountCodes_WhenCalled() throws Exception {

    List<DiscountCodeResponse> discountCodeResponses =
        Arrays.asList(
            DiscountCodeResponse.builder()
                .id(1L)
                .code("siema")
                .discountPercentage(BigDecimal.valueOf(50))
                .expirationDate(LocalDate.now())
                .remainingUses(1L)
                .build(),
            DiscountCodeResponse.builder()
                .id(2L)
                .code("kodzik")
                .discountPercentage(BigDecimal.valueOf(30))
                .expirationDate(LocalDate.now())
                .remainingUses(1L)
                .build());

    when(discountCodesService.getDiscountCodes()).thenReturn(discountCodeResponses);

    mockMvc
        .perform(get("/api/v1/discount-codes/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].code", is("siema")))
        .andExpect(jsonPath("$[0].discount_percentage", is(50)))
        .andExpect(jsonPath("$[1].id", is(2)));

    verify(discountCodesService, times(1)).getDiscountCodes();
  }

  @Test
  public void getDiscountCode_ShouldReturnDiscountCode_WhenCalled() throws Exception {

    DiscountCodeResponse discountCodeResponse =
        DiscountCodeResponse.builder()
            .id(30L)
            .code("siema")
            .discountPercentage(BigDecimal.valueOf(50))
            .expirationDate(LocalDate.now())
            .remainingUses(1L)
            .build();

    when(discountCodesService.getDiscountCode("siema")).thenReturn(discountCodeResponse);

    mockMvc
        .perform(get("/api/v1/discount-codes/siema"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code", is("siema")))
        .andExpect(jsonPath("$.discount_percentage", is(50)))
        .andExpect(jsonPath("$.id", is(30)));

    verify(discountCodesService, times(1)).getDiscountCode("siema");
  }

  @Test
  public void getDiscountCode_ShouldReturnNotFound_WhenDiscountCodeDoesNotExist() throws Exception {

    when(discountCodesService.getDiscountCode("kodzik"))
        .thenThrow(new DiscountCodeNotFoundException("kodzik"));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("discountCode.notExists", null, Locale.forLanguageTag("en")))
        .thenReturn("This discount code does not exist!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new DiscountCodesController(discountCodesService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(get("/api/v1/discount-codes/kodzik").header("Accept-Language", "en"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message", is("This discount code does not exist!")))
        .andExpect(jsonPath("$.status_code", is(HttpStatus.NOT_FOUND.value())));

    verify(discountCodesService, times(1)).getDiscountCode("kodzik");
  }

  @Test
  public void getDiscountCode_ShouldReturnGone_WhenDiscountCodeExpired() throws Exception {

    when(discountCodesService.getDiscountCode("kodzik"))
        .thenThrow(new DiscountCodeExpiredException("kodzik"));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("discountCode.expired", null, Locale.forLanguageTag("en")))
        .thenReturn("This discount code already expired!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new DiscountCodesController(discountCodesService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(get("/api/v1/discount-codes/kodzik").header("Accept-Language", "en"))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.message", is("This discount code already expired!")))
        .andExpect(jsonPath("$.status_code", is(HttpStatus.GONE.value())));

    verify(discountCodesService, times(1)).getDiscountCode("kodzik");
  }

  @Test
  public void addDiscountCode_ShouldReturnOk_WhenValidRequest() throws Exception {

    NewDiscountCodeRequest request =
        NewDiscountCodeRequest.builder()
            .code("kodzik")
            .discountPercentage(BigDecimal.valueOf(30))
            .expirationDate(LocalDate.now().plusMonths(1))
            .remainingUses(3L)
            .build();

    NewDiscountCodeResponse response =
        NewDiscountCodeResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully added new discount code: 'kodzik'")
            .build();

    when(discountCodesService.addDiscountCode(request)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/discount-codes/add-discount-code")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
        .andExpect(jsonPath("$.message", is("Successfully added new discount code: 'kodzik'")));
  }

  @Test
  public void addDiscountCode_ShouldReturnConflict_WhenDiscountCodeAlreadyExist() throws Exception {

    NewDiscountCodeRequest request =
        NewDiscountCodeRequest.builder()
            .code("kodzik")
            .discountPercentage(BigDecimal.valueOf(30))
            .expirationDate(LocalDate.now().plusMonths(1))
            .remainingUses(3L)
            .build();

    when(discountCodesService.addDiscountCode(request))
        .thenThrow(new DiscountCodeAlreadyExistsException("kodzik"));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("discountCode.alreadyExists", null, Locale.forLanguageTag("en")))
        .thenReturn("Such discount code already exists!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new DiscountCodesController(discountCodesService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(
            post("/api/v1/discount-codes/add-discount-code")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.CONFLICT.value())))
        .andExpect(jsonPath("$.message", is("Such discount code already exists!")));

    verify(discountCodesService, times(1)).addDiscountCode(request);
  }

  @Test
  public void addDiscountCode_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

    NewDiscountCodeRequest request = NewDiscountCodeRequest.builder().code("kod").build();

    mockMvc
        .perform(
            post("/api/v1/discount-codes/add-discount-code")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void addDiscountCode_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

    NewDiscountCodeRequest request =
        NewDiscountCodeRequest.builder()
            .code("kodzik")
            .discountPercentage(BigDecimal.valueOf(30))
            .expirationDate(LocalDate.now().plusMonths(1))
            .remainingUses(3L)
            .build();

    mockMvc
        .perform(
            post("/api/v1/discount-codes/add-discount-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void addDiscountCode_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

    String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

    for (String header : invalidHeaders) {
      mockMvc
          .perform(
              post("/api/v1/discount-codes/add-discount-code").header("Accept-Language", header))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  public void updateDiscountCode_ShouldReturnOk_WhenValidRequest() throws Exception {

    DiscountCode existingDiscountCode =
        new DiscountCode("kodzik", BigDecimal.valueOf(50), LocalDate.now().plusMonths(1), 2L);

    UpdatedDiscountCodeRequest request =
        UpdatedDiscountCodeRequest.builder()
            .code("kodzik")
            .newCode("siema")
            .remainingUses(10L)
            .build();

    UpdatedDiscountCodeResponse response =
        UpdatedDiscountCodeResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully updated discount code 'kodzik'")
            .build();

    when(discountCodesService.findDiscountCodeByCode(request.code()))
        .thenReturn(existingDiscountCode);
    when(discountCodesService.updateDiscountCode(existingDiscountCode, request))
        .thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/discount-codes/update-discount-code")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
        .andExpect(jsonPath("$.message", is("Successfully updated discount code 'kodzik'")));

    verify(discountCodesService, times(1)).findDiscountCodeByCode(request.code());
    verify(discountCodesService, times(1)).updateDiscountCode(existingDiscountCode, request);
  }

  @Test
  public void updateDiscountCode_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

    UpdatedDiscountCodeRequest request =
        UpdatedDiscountCodeRequest.builder().newCode("kod").build();

    mockMvc
        .perform(
            put("/api/v1/discount-codes/update-discount-code")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateDiscountCode_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

    UpdatedDiscountCodeRequest request =
        UpdatedDiscountCodeRequest.builder()
            .code("kodzik")
            .newCode("siema")
            .remainingUses(10L)
            .build();

    mockMvc
        .perform(
            put("/api/v1/discount-codes/update-discount-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateDiscountCode_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

    String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

    for (String header : invalidHeaders) {
      mockMvc
          .perform(
              put("/api/v1/discount-codes/update-discount-code").header("Accept-Language", header))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  public void removeDiscountCode_ShouldReturnOk_WhenValidRequest() throws Exception {

    DiscountCode existingDiscountCode =
        new DiscountCode("kodzik", BigDecimal.valueOf(50), LocalDate.now().plusMonths(1), 2L);
    RemovedDiscountCodeRequest request = new RemovedDiscountCodeRequest("kodzik");
    RemovedDiscountCodeResponse response =
        RemovedDiscountCodeResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully removed discount code 'kodzik'")
            .build();

    when(discountCodesService.findDiscountCodeByCode(existingDiscountCode.getCode()))
        .thenReturn(existingDiscountCode);
    when(discountCodesService.removeDiscountCode(any(DiscountCode.class))).thenReturn(response);

    mockMvc
        .perform(
            delete("/api/v1/discount-codes/remove-discount-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
        .andExpect(jsonPath("$.message", is("Successfully removed discount code 'kodzik'")));

    verify(discountCodesService, times(1)).findDiscountCodeByCode(request.code());
    verify(discountCodesService, times(1)).removeDiscountCode(existingDiscountCode);
  }
}
