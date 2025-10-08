package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.Order;
import com.p4zd4n.kebab.enums.OrderStatus;
import com.p4zd4n.kebab.enums.OrderType;
import com.p4zd4n.kebab.exceptions.GlobalExceptionHandler;
import com.p4zd4n.kebab.exceptions.expired.DiscountCodeExpiredException;
import com.p4zd4n.kebab.exceptions.expired.TrackOrderExpiredException;
import com.p4zd4n.kebab.exceptions.notfound.DiscountCodeNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.OrderNotFoundException;
import com.p4zd4n.kebab.exceptions.notmatches.TrackOrderDataDoesNotMatchException;
import com.p4zd4n.kebab.requests.orders.NewOrderRequest;
import com.p4zd4n.kebab.requests.orders.RemovedOrderRequest;
import com.p4zd4n.kebab.requests.orders.TrackOrderRequest;
import com.p4zd4n.kebab.requests.orders.UpdatedOrderRequest;
import com.p4zd4n.kebab.responses.orders.NewOrderResponse;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.responses.orders.RemovedOrderResponse;
import com.p4zd4n.kebab.responses.orders.UpdatedOrderResponse;
import com.p4zd4n.kebab.services.orders.OrdersService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrdersControllerTest {

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @MockBean private OrdersService ordersService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getOrders_ShouldReturnOrders_WhenCalled() throws Exception {

    List<OrderResponse> orders =
        Arrays.asList(
            OrderResponse.builder()
                .orderType(OrderType.ON_SITE)
                .orderStatus(OrderStatus.RECEIVED)
                .customerPhone("123456789")
                .customerEmail("example@example.com")
                .build(),
            OrderResponse.builder()
                .orderType(OrderType.ON_SITE)
                .orderStatus(OrderStatus.RECEIVED)
                .customerPhone("321456789")
                .customerEmail("ex@example.com")
                .build());

    when(ordersService.getOrders()).thenReturn(orders);

    mockMvc
        .perform(get("/api/v1/orders/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].customer_email", is("example@example.com")))
        .andExpect(jsonPath("$[0].customer_phone", is("123456789")))
        .andExpect(jsonPath("$[1].order_type", is("ON_SITE")))
        .andExpect(jsonPath("$[1].customer_phone", is("321456789")));

    verify(ordersService, times(1)).getOrders();
  }

  @Test
  public void addOrder_ShouldReturnOk_WhenValidRequest() throws Exception {

    String language = "en";

    NewOrderRequest request =
        NewOrderRequest.builder()
            .orderType(OrderType.ON_SITE)
            .orderStatus(OrderStatus.RECEIVED)
            .customerPhone("321456789")
            .customerEmail("ex@example.com")
            .build();

    NewOrderResponse response =
        NewOrderResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully added new order")
            .build();

    when(ordersService.addOrder(request, language)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/orders/add-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
        .andExpect(jsonPath("$.message", is("Successfully added new order")));
  }

  @Test
  public void addOrder_ShouldReturnNotFound_WhenEnteredDiscountCodeNotExist() throws Exception {

    String language = "en";

    NewOrderRequest request =
        NewOrderRequest.builder()
            .orderType(OrderType.ON_SITE)
            .orderStatus(OrderStatus.RECEIVED)
            .customerPhone("321456789")
            .customerEmail("ex@example.com")
            .discountCode("kodzik")
            .build();

    when(ordersService.addOrder(request, language))
        .thenThrow(new DiscountCodeNotFoundException("kodzik"));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("discountCode.notExists", null, Locale.forLanguageTag("en")))
        .thenReturn("This discount code does not exist!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new OrdersController(ordersService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(
            post("/api/v1/orders/add-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.NOT_FOUND.value())))
        .andExpect(jsonPath("$.message", is("This discount code does not exist!")));

    verify(ordersService, times(1)).addOrder(request, language);
  }

  @Test
  public void addOrder_ShouldReturnGone_WhenEnteredDiscountCodeExpired() throws Exception {

    String language = "en";

    NewOrderRequest request =
        NewOrderRequest.builder()
            .orderType(OrderType.ON_SITE)
            .orderStatus(OrderStatus.RECEIVED)
            .customerPhone("321456789")
            .customerEmail("ex@example.com")
            .discountCode("kodzik")
            .build();

    when(ordersService.addOrder(request, language))
        .thenThrow(new DiscountCodeExpiredException("kodzik"));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("discountCode.expired", null, Locale.forLanguageTag("en")))
        .thenReturn("This discount code already expired!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new OrdersController(ordersService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(
            post("/api/v1/orders/add-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.GONE.value())))
        .andExpect(jsonPath("$.message", is("This discount code already expired!")));

    verify(ordersService, times(1)).addOrder(request, language);
  }

  @Test
  public void addOrder_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

    NewOrderRequest request = NewOrderRequest.builder().customerEmail("ex@example.com").build();

    mockMvc
        .perform(
            post("/api/v1/orders/add-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void addOrder_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

    NewOrderRequest request =
        NewOrderRequest.builder()
            .orderType(OrderType.ON_SITE)
            .orderStatus(OrderStatus.RECEIVED)
            .customerPhone("321456789")
            .customerEmail("ex@example.com")
            .discountCode("kodzik")
            .build();

    mockMvc
        .perform(
            post("/api/v1/orders/add-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void addOrder_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

    String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

    for (String header : invalidHeaders) {
      mockMvc
          .perform(post("/api/v1/orders/add-order").header("Accept-Language", header))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  public void trackOrder_ShouldReturnOk_WhenValidRequest() throws Exception {

    TrackOrderRequest request =
        TrackOrderRequest.builder().id(10L).customerPhone("321456789").build();

    OrderResponse response =
        OrderResponse.builder()
            .orderType(OrderType.ON_SITE)
            .orderStatus(OrderStatus.RECEIVED)
            .customerPhone("321456789")
            .customerEmail("example@example.com")
            .build();

    when(ordersService.trackOrder(request)).thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/orders/track-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customer_phone", is("321456789")))
        .andExpect(jsonPath("$.customer_email", is("example@example.com")));
  }

  @Test
  public void trackOrder_ShouldReturnNotFound_WhenOrderNotExists() throws Exception {

    TrackOrderRequest request =
        TrackOrderRequest.builder().id(10L).customerPhone("321456789").build();

    when(ordersService.trackOrder(request)).thenThrow(new OrderNotFoundException(request.id()));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("order.notExists", null, Locale.forLanguageTag("en")))
        .thenReturn("Order with this ID does not exist!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new OrdersController(ordersService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(
            post("/api/v1/orders/track-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.NOT_FOUND.value())))
        .andExpect(jsonPath("$.message", is("Order with this ID does not exist!")));

    verify(ordersService, times(1)).trackOrder(request);
  }

  @Test
  public void trackOrder_ShouldReturnNotFound_WhenTrackOrderDataDoesNotMatch() throws Exception {

    TrackOrderRequest request =
        TrackOrderRequest.builder().id(10L).customerPhone("321456789").build();

    when(ordersService.trackOrder(request))
        .thenThrow(new TrackOrderDataDoesNotMatchException(request.id(), request.customerPhone()));

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("orderData.notMatching", null, Locale.forLanguageTag("en")))
        .thenReturn("Entered ID and phone number do not match any existing order!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new OrdersController(ordersService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(
            post("/api/v1/orders/track-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.NOT_FOUND.value())))
        .andExpect(
            jsonPath(
                "$.message", is("Entered ID and phone number do not match any existing order!")));

    verify(ordersService, times(1)).trackOrder(request);
  }

  @Test
  public void trackOrder_ShouldReturnGone_WhenTrackOrderExpired() throws Exception {

    TrackOrderRequest request =
        TrackOrderRequest.builder().id(10L).customerPhone("321456789").build();

    when(ordersService.trackOrder(request)).thenThrow(new TrackOrderExpiredException());

    MessageSource messageSource = mock(MessageSource.class);
    when(messageSource.getMessage("trackOrder.expired", null, Locale.forLanguageTag("en")))
        .thenReturn(
            "Tracking for this order is no longer available, because last update was over 2 hours ago!");

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new OrdersController(ordersService))
            .setControllerAdvice(exceptionHandler)
            .build();

    mockMvc
        .perform(
            post("/api/v1/orders/track-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.GONE.value())))
        .andExpect(
            jsonPath(
                "$.message",
                is(
                    "Tracking for this order is no longer available, because last update was over 2 hours ago!")));

    verify(ordersService, times(1)).trackOrder(request);
  }

  @Test
  public void trackOrder_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

    TrackOrderRequest request = TrackOrderRequest.builder().id(10L).build();

    mockMvc
        .perform(
            post("/api/v1/orders/track-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void trackOrder_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

    TrackOrderRequest request =
        TrackOrderRequest.builder().id(10L).customerPhone("321456789").build();

    mockMvc
        .perform(
            post("/api/v1/orders/track-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void trackOrder_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

    String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

    for (String header : invalidHeaders) {
      mockMvc
          .perform(post("/api/v1/orders/track-order").header("Accept-Language", header))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  public void updateOrder_ShouldReturnOk_WhenValidRequest() throws Exception {

    Order existingOrder =
        Order.builder()
            .orderStatus(OrderStatus.IN_PREPARATION)
            .orderType(OrderType.ON_SITE)
            .customerPhone("123456789")
            .build();
    existingOrder.setId(1L);

    UpdatedOrderRequest request =
        UpdatedOrderRequest.builder().id(1L).orderStatus(OrderStatus.READY).build();

    UpdatedOrderResponse response =
        UpdatedOrderResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully updated order with id '" + existingOrder.getId() + "'")
            .build();

    when(ordersService.findOrderById(request.id())).thenReturn(existingOrder);
    when(ordersService.updateOrder(existingOrder, request)).thenReturn(response);

    mockMvc
        .perform(
            put("/api/v1/orders/update-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
        .andExpect(
            jsonPath(
                "$.message",
                is("Successfully updated order with id '" + existingOrder.getId() + "'")));

    verify(ordersService, times(1)).findOrderById(request.id());
    verify(ordersService, times(1)).updateOrder(existingOrder, request);
  }

  @Test
  public void updateOrder_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

    UpdatedOrderRequest request =
        UpdatedOrderRequest.builder().customerEmail("ex@example.com").build();

    mockMvc
        .perform(
            put("/api/v1/orders/update-order")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateOrder_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

    UpdatedOrderRequest request =
        UpdatedOrderRequest.builder().id(1L).orderStatus(OrderStatus.READY).build();

    mockMvc
        .perform(
            put("/api/v1/orders/update-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void updateOrder_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

    String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

    for (String header : invalidHeaders) {
      mockMvc
          .perform(put("/api/v1/orders/update-order").header("Accept-Language", header))
          .andExpect(status().isBadRequest());
    }
  }

  @Test
  public void removeOrder_ShouldReturnOk_WhenValidRequest() throws Exception {

    Order existingOrder =
        Order.builder()
            .orderStatus(OrderStatus.IN_PREPARATION)
            .orderType(OrderType.ON_SITE)
            .customerPhone("123456789")
            .build();
    existingOrder.setId(1L);

    RemovedOrderRequest request = new RemovedOrderRequest(1L);
    RemovedOrderResponse response =
        RemovedOrderResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully removed order with id '" + request.id() + "'")
            .build();

    when(ordersService.findOrderById(request.id())).thenReturn(existingOrder);
    when(ordersService.removeOrder(any(Order.class))).thenReturn(response);

    mockMvc
        .perform(
            delete("/api/v1/orders/remove-order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
        .andExpect(
            jsonPath("$.message", is("Successfully removed order with id '" + request.id() + "'")));

    verify(ordersService, times(1)).findOrderById(request.id());
    verify(ordersService, times(1)).removeOrder(existingOrder);
  }
}
