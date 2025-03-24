package com.p4zd4n.kebab.services.orders;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.repositories.OrdersRepository;
import com.p4zd4n.kebab.requests.orders.NewOrderRequest;
import com.p4zd4n.kebab.responses.orders.NewOrderResponse;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MealRepository mealRepository;
    private final BeverageRepository beverageRepository;
    private final AddonRepository addonRepository;

    public OrdersService(OrdersRepository ordersRepository, MealRepository mealRepository, BeverageRepository beverageRepository, AddonRepository addonRepository) {
        this.ordersRepository = ordersRepository;
        this.mealRepository = mealRepository;
        this.beverageRepository = beverageRepository;
        this.addonRepository = addonRepository;
    }

    public List<OrderResponse> getOrders() {

        log.info("Started retrieving orders");

        List<Order> orders = ordersRepository.findAll();

        List<OrderResponse> response = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved orders");

        return response;
    }

    public OrderResponse mapToResponse(Order order) {

        List<OrderMeal> meals = order.getOrderMeals().stream()
                .map(orderMeal -> OrderMeal.builder()
                        .order(order)
                        .meal(orderMeal.getMeal())
                        .quantity(orderMeal.getQuantity())
                        .build())
                .toList();

        List<OrderBeverage> beverages = order.getOrderBeverages().stream()
                .map(orderBeverage -> OrderBeverage.builder()
                        .order(order)
                        .beverage(orderBeverage.getBeverage())
                        .quantity(orderBeverage.getQuantity())
                        .build())
                .toList();

        List<OrderAddon> addons = order.getOrderAddons().stream()
                .map(orderAddon -> OrderAddon.builder()
                        .order(order)
                        .addon(orderAddon.getAddon())
                        .quantity(orderAddon.getQuantity())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .street(order.getStreet())
                .houseNumber(order.getHouseNumber())
                .postalCode(order.getPostalCode())
                .city(order.getCity())
                .meals(meals)
                .beverages(beverages)
                .addons(addons)
                .build();
    }

    public NewOrderResponse addOrder(NewOrderRequest request) {

        Order order = Order.builder()
                .orderType(request.orderType())
                .orderStatus(request.orderStatus())
                .customerPhone(request.customerPhone())
                .customerEmail(request.customerEmail())
                .build();

        if (request.street() != null && !request.street().isBlank()) {
            order.setStreet(request.street());
        }

        if (request.houseNumber() != null && request.houseNumber() > 0) {
            order.setHouseNumber(request.houseNumber());
        }

        if (request.postalCode() != null && !request.postalCode().isBlank()) {
            order.setPostalCode(request.postalCode());
        }

        if (request.city() != null && !request.city().isBlank()) {
            order.setCity(request.city());
        }

        Order savedOrder = ordersRepository.save(order);

        if (request.meals() != null) {
            Map<String, Integer> mealQuantities = request.meals();
            List<Meal> meals = mealRepository.findAllByNameIn(mealQuantities.keySet());

            for (Meal meal : meals) {
                Integer quantity = mealQuantities.get(meal.getName());
                if (quantity != null && quantity > 0) savedOrder.addMeal(meal, quantity);
            }
        }

        if (request.beverages() != null && !request.beverages().isEmpty()) {
            Map<String, Map<BigDecimal, Integer>> beverageQuantities = request.beverages();

            for (Map.Entry<String, Map<BigDecimal, Integer>> entry : beverageQuantities.entrySet()) {
                String beverageName = entry.getKey();
                Map<BigDecimal, Integer> capacityQuantities = entry.getValue();

                for (Map.Entry<BigDecimal, Integer> capacityEntry : capacityQuantities.entrySet()) {
                    BigDecimal capacity = capacityEntry.getKey();
                    Integer quantity = capacityEntry.getValue();

                    if (quantity != null && quantity > 0) {
                        Beverage beverage = beverageRepository.findByNameAndCapacity(beverageName, capacity)
                                .orElse(null);

                        if (beverage != null) savedOrder.addBeverage(beverage, quantity);
                    }
                }
            }
        }

        if (request.addons() != null && !request.addons().isEmpty()) {
            Map<String, Integer> addonQuantities = request.addons();
            List<Addon> addons = addonRepository.findAllByNameIn(addonQuantities.keySet());

            for (Addon addon : addons) {
                Integer quantity = addonQuantities.get(addon.getName());
                if (quantity != null && quantity > 0) savedOrder.addAddon(addon, quantity);
            }
        }

        savedOrder = ordersRepository.save(savedOrder);

        return NewOrderResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new order with id '" + savedOrder.getId() + "'")
                .build();
    }
}
