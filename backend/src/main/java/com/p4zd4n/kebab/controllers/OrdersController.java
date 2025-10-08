package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.Order;
import com.p4zd4n.kebab.requests.orders.NewOrderRequest;
import com.p4zd4n.kebab.requests.orders.RemovedOrderRequest;
import com.p4zd4n.kebab.requests.orders.TrackOrderRequest;
import com.p4zd4n.kebab.requests.orders.UpdatedOrderRequest;
import com.p4zd4n.kebab.responses.orders.NewOrderResponse;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.responses.orders.RemovedOrderResponse;
import com.p4zd4n.kebab.responses.orders.UpdatedOrderResponse;
import com.p4zd4n.kebab.services.orders.OrdersService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class OrdersController {

  private final OrdersService ordersService;

  public OrdersController(OrdersService ordersService) {
    this.ordersService = ordersService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<OrderResponse>> getOrders() {
    log.info("Received get orders request");
    return ResponseEntity.ok(ordersService.getOrders());
  }

  @PostMapping("/add-order")
  public ResponseEntity<NewOrderResponse> addOrder(
      @RequestHeader(value = "Accept-Language") String language,
      @Valid @RequestBody NewOrderRequest request)
      throws MessagingException {
    LanguageValidator.validateLanguage(language);

    log.info("Received add order request");

    NewOrderResponse response = ordersService.addOrder(request, language);

    log.info("Successfully added new order");

    return ResponseEntity.ok(response);
  }

  @PostMapping("/track-order")
  public ResponseEntity<OrderResponse> trackOrder(
      @RequestHeader(value = "Accept-Language") String language,
      @Valid @RequestBody TrackOrderRequest request) {
    LanguageValidator.validateLanguage(language);

    log.info("Received track order request");

    OrderResponse response = ordersService.trackOrder(request);

    log.info("Successfully started tracking order");

    return ResponseEntity.ok(response);
  }

  @PutMapping("/update-order")
  public ResponseEntity<UpdatedOrderResponse> updateOrder(
      @RequestHeader(value = "Accept-Language") String language,
      @Valid @RequestBody UpdatedOrderRequest request) {
    LanguageValidator.validateLanguage(language);

    log.info("Received update order request");

    Order existingOrder = ordersService.findOrderById(request.id());
    UpdatedOrderResponse response = ordersService.updateOrder(existingOrder, request);

    log.info("Successfully updated order");

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/remove-order")
  public ResponseEntity<RemovedOrderResponse> removeOrder(
      @Valid @RequestBody RemovedOrderRequest request) {
    log.info("Received remove order request");

    Order existingOrder = ordersService.findOrderById(request.id());
    RemovedOrderResponse response = ordersService.removeOrder(existingOrder);

    return ResponseEntity.ok(response);
  }
}
