package com.p4zd4n.kebab.services.orders;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.entities.key.MealKey;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.expired.DiscountCodeExpiredException;
import com.p4zd4n.kebab.exceptions.expired.TrackOrderExpiredException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidMealKeyFormatException;
import com.p4zd4n.kebab.exceptions.notfound.DiscountCodeNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.IngredientNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.OrderNotFoundException;
import com.p4zd4n.kebab.exceptions.notmatches.TrackOrderDataDoesNotMatchException;
import com.p4zd4n.kebab.repositories.*;
import com.p4zd4n.kebab.requests.orders.NewOrderRequest;
import com.p4zd4n.kebab.requests.orders.TrackOrderRequest;
import com.p4zd4n.kebab.requests.orders.UpdatedOrderRequest;
import com.p4zd4n.kebab.responses.orders.NewOrderResponse;
import com.p4zd4n.kebab.responses.orders.OrderResponse;
import com.p4zd4n.kebab.responses.orders.RemovedOrderResponse;
import com.p4zd4n.kebab.responses.orders.UpdatedOrderResponse;
import com.p4zd4n.kebab.utils.mails.DiscountCodeSendable;
import com.p4zd4n.kebab.utils.mails.HighValueOrderRewardMailUtil;
import com.p4zd4n.kebab.utils.mails.TenOrdersRewardMailUtil;
import com.p4zd4n.kebab.utils.mails.ThanksForOrderMailUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final MealRepository mealRepository;
    private final BeverageRepository beverageRepository;
    private final AddonRepository addonRepository;
    private final IngredientRepository ingredientRepository;
    private final CustomerRepository customerRepository;
    private final ThanksForOrderMailUtil thanksForOrderMailUtil;
    private final TenOrdersRewardMailUtil tenOrdersRewardMailUtil;
    private final HighValueOrderRewardMailUtil highValueOrderRewardMailUtil;
    private final DiscountCodesRepository discountCodesRepository;

    public OrdersService(
            OrdersRepository ordersRepository,
            MealRepository mealRepository,
            BeverageRepository beverageRepository,
            AddonRepository addonRepository,
            IngredientRepository ingredientRepository,
            CustomerRepository customerRepository,
            ThanksForOrderMailUtil thanksForOrderMailUtil,
            TenOrdersRewardMailUtil tenOrdersRewardMailUtil,
            HighValueOrderRewardMailUtil highValueOrderRewardMailUtil,
            DiscountCodesRepository discountCodesRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.mealRepository = mealRepository;
        this.beverageRepository = beverageRepository;
        this.addonRepository = addonRepository;
        this.ingredientRepository = ingredientRepository;
        this.customerRepository = customerRepository;
        this.thanksForOrderMailUtil = thanksForOrderMailUtil;
        this.tenOrdersRewardMailUtil = tenOrdersRewardMailUtil;
        this.discountCodesRepository = discountCodesRepository;
        this.highValueOrderRewardMailUtil = highValueOrderRewardMailUtil;
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
                .map(orderMeal -> {
                    return OrderMeal.builder()
                            .order(order)
                            .mealName(orderMeal.getMealName())
                            .finalPrice(orderMeal.getFinalPrice())
                            .size(orderMeal.getSize())
                            .quantity(orderMeal.getQuantity())
                            .ingredientNames(orderMeal.getIngredientNames())
                            .build();
                })
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
            .additionalComments(order.getAdditionalComments())
            .totalPrice(order.getTotalPrice())
            .meals(meals)
            .beverages(beverages)
            .addons(addons)
            .build();
    }

    public NewOrderResponse addOrder(NewOrderRequest request, String language) throws MessagingException {

        Order order = Order.builder()
            .orderType(request.orderType())
            .orderStatus(request.orderStatus())
            .customerPhone(request.customerPhone())
            .build();

        Order savedOrder = ordersRepository.save(order);

        if (request.customerEmail() != null && !request.customerEmail().isBlank()) {
            order.setCustomerEmail(request.customerEmail());

            Optional<Customer> optionalCustomer = customerRepository.findCustomerByEmail(request.customerEmail());
            if (optionalCustomer.isPresent()) {
                Customer customer = optionalCustomer.get();
                customer.setOrderCount(customer.getOrderCount() + 1);

                if (language.equals("pl"))
                    thanksForOrderMailUtil.sendPl(request, customer, savedOrder.getId());
                else
                    thanksForOrderMailUtil.sendEng(request, customer, savedOrder.getId());

                if (customer.getOrderCount() % 10 == 0) {
                    generateAndSendDiscountCode(tenOrdersRewardMailUtil, language, request);
                }
            } else {
                Customer newCustomer = Customer.builder()
                        .email(request.customerEmail())
                        .build();

                customerRepository.save(newCustomer);

                if (language.equals("pl"))
                    thanksForOrderMailUtil.sendPl(request, newCustomer, savedOrder.getId());
                else
                    thanksForOrderMailUtil.sendEng(request, newCustomer, savedOrder.getId());
            }
        }

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

        if (request.additionalComments() != null && !request.additionalComments().isBlank()) {
            order.setAdditionalComments(request.additionalComments());
        }

        savedOrder = ordersRepository.save(order);
        BigDecimal totalPriceBeforeDelivery = BigDecimal.ZERO;

        if (request.meals() != null)  {
            Map<MealKey, Map<Size, Integer>> mealQuantities = request.meals().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> parseMealKey(entry.getKey()),
                        Map.Entry::getValue
                ));

            addMeals(order, mealQuantities);
            totalPriceBeforeDelivery = totalPriceBeforeDelivery.add(getMealTotalPrice(mealQuantities));
        }

        if (request.beverages() != null && !request.beverages().isEmpty()) {
            addBeverages(order, request.beverages());
            totalPriceBeforeDelivery = totalPriceBeforeDelivery.add(getBeverageTotalPrice(request.beverages()));
        }

        if (request.addons() != null && !request.addons().isEmpty()) {
            addAddons(order, request.addons());
            totalPriceBeforeDelivery = totalPriceBeforeDelivery.add(getAddonTotalPrice(request.addons()));
        }

        BigDecimal discountedPrice = totalPriceBeforeDelivery;

        if (request.discountCode() != null && !request.discountCode().isBlank()) {
            DiscountCode discountCode = discountCodesRepository.findByCode(request.discountCode())
                    .orElseThrow(() -> new DiscountCodeNotFoundException(request.discountCode()));

            if (discountCode.getExpirationDate().isBefore(LocalDate.now()) || discountCode.getRemainingUses() < 1) {
                throw new DiscountCodeExpiredException(discountCode.getCode());
            }

            BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                    discountCode.getDiscountPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            discountedPrice = discountedPrice.multiply(discountMultiplier);

            discountCode.setRemainingUses(discountCode.getRemainingUses() - 1);
            discountCodesRepository.save(discountCode);
        }

        BigDecimal finalTotalPrice = discountedPrice;

        if (finalTotalPrice.compareTo(BigDecimal.valueOf(100)) > 0 && request.customerEmail() != null && !request.customerEmail().isBlank()) {
            generateAndSendDiscountCode(highValueOrderRewardMailUtil, language, request);
        }

        if (order.getStreet() != null && order.getHouseNumber() != null && order.getPostalCode() != null && order.getCity() != null) {
            finalTotalPrice = finalTotalPrice.add(BigDecimal.valueOf(15));
        }

        savedOrder.setTotalPrice(finalTotalPrice);

        savedOrder = ordersRepository.save(savedOrder);

        return NewOrderResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully added new order with id '" + savedOrder.getId() + "'")
            .id(savedOrder.getId())
            .build();
    }

    private void generateAndSendDiscountCode(
            DiscountCodeSendable sendable, String language, NewOrderRequest request
    ) throws MessagingException {

        BigDecimal randomPercentage = BigDecimal.valueOf(
                ThreadLocalRandom.current().nextInt(10, 21)
        ).setScale(2, RoundingMode.HALF_UP);

        DiscountCode discountCode = DiscountCode.builder()
                .remainingUses(1L)
                .expirationDate(LocalDate.now().plusMonths(1))
                .discountPercentage(randomPercentage)
                .build();

        discountCodesRepository.save(discountCode);

        if (sendable instanceof HighValueOrderRewardMailUtil) {
            if (language.equals("pl"))
                highValueOrderRewardMailUtil.sendPl(request, discountCode);
            else
                highValueOrderRewardMailUtil.sendEng(request, discountCode);
        } else if (sendable instanceof TenOrdersRewardMailUtil) {
            if (language.equals("pl"))
                tenOrdersRewardMailUtil.sendPl(request, discountCode);
            else
                tenOrdersRewardMailUtil.sendEng(request, discountCode);
        }
    }

    private BigDecimal getMealTotalPrice(Map<MealKey, Map<Size, Integer>> mealQuantities) {

        AtomicReference<BigDecimal> totalPrice = new AtomicReference<>(BigDecimal.ZERO);

        mealQuantities.forEach((mealKey, sizeQuantities) -> {
            Optional<Meal> optionalMeal = mealRepository.findByName(mealKey.getMealName());

            if (optionalMeal.isEmpty()) return;

            Meal meal = optionalMeal.get();
            sizeQuantities.forEach((size, quantity) -> {
                if (quantity != null && quantity > 0) {
                    BigDecimal basePrice = meal.getPrices().get(size);
                    BigDecimal discount = meal.getPromotions().stream()
                            .filter(promotion -> promotion.getSizes().contains(size))
                            .map(MealPromotion::getDiscountPercentage)
                            .findFirst()
                            .orElse(BigDecimal.ZERO);

                    BigDecimal discountFraction = discount.divide(BigDecimal.valueOf(100));
                    BigDecimal discountedPrice = basePrice.subtract(basePrice.multiply(discountFraction));
                    BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(quantity));

                    totalPrice.updateAndGet(current -> current.add(subtotal));
                }
            });
        });

        return totalPrice.get();
    }

    private BigDecimal getBeverageTotalPrice(Map<String, Map<BigDecimal, Integer>> beverageQuantities) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<String, Map<BigDecimal, Integer>> entry : beverageQuantities.entrySet()) {
            String beverageName = entry.getKey();
            Map<BigDecimal, Integer> capacityQuantities = entry.getValue();

            for (Map.Entry<BigDecimal, Integer> capacityEntry : capacityQuantities.entrySet()) {
                BigDecimal capacity = capacityEntry.getKey();
                Integer quantity = capacityEntry.getValue();
                Beverage beverage = beverageRepository.findByNameAndCapacity(beverageName, capacity).orElse(null);

                if (beverage == null) continue;

                BigDecimal basePrice = beverage.getPrice();
                BigDecimal discount = beverage.getPromotion() != null
                        ? beverage.getPromotion().getDiscountPercentage()
                        : BigDecimal.ZERO;
                BigDecimal discountFraction = discount.divide(BigDecimal.valueOf(100));
                BigDecimal discountedPrice = basePrice.subtract(basePrice.multiply(discountFraction));
                BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(quantity));

                totalPrice = totalPrice.add(subtotal);
            }
        }

        return totalPrice;
    }

    private BigDecimal getAddonTotalPrice(Map<String, Integer> addonQuantities) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Addon> addons = addonRepository.findAllByNameIn(addonQuantities.keySet());

        for (Addon addon : addons) {
            Integer quantity = addonQuantities.get(addon.getName());
            BigDecimal basePrice = addon.getPrice();
            BigDecimal discount = addon.getPromotion() != null
                    ? addon.getPromotion().getDiscountPercentage()
                    : BigDecimal.ZERO;
            BigDecimal discountFraction = discount.divide(BigDecimal.valueOf(100));
            BigDecimal discountedPrice = basePrice.subtract(basePrice.multiply(discountFraction));
            BigDecimal subtotal = discountedPrice.multiply(BigDecimal.valueOf(quantity));

            totalPrice = totalPrice.add(subtotal);
        }

        return totalPrice;
    }

    public Order findOrderById(Long id) {

        log.info("Started finding order with id '{}'", id);

        Order order = ordersRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        log.info("Successfully found order with id '{}'", id);

        return order;
    }

    public OrderResponse trackOrder(TrackOrderRequest request) {

        Order order = ordersRepository.findById(request.id())
            .orElseThrow(() -> new OrderNotFoundException(request.id()));

        if (!order.getCustomerPhone().equals(request.customerPhone())) {
            throw new TrackOrderDataDoesNotMatchException(request.id(), request.customerPhone());
        }

        if (order.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(2))) {
            throw new TrackOrderExpiredException();
        }

        return mapToResponse(order);
    }

    public UpdatedOrderResponse updateOrder(Order order, UpdatedOrderRequest request) {

        UpdatedOrderResponse response = UpdatedOrderResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully updated order with id '" + order.getId() + "'")
            .build();

        if (request.orderType() != null) order.setOrderType(request.orderType());
        if (request.orderStatus() != null) order.setOrderStatus(request.orderStatus());
        if (request.customerEmail() != null) order.setCustomerEmail(request.customerEmail());
        if (request.customerPhone() != null) order.setCustomerPhone(request.customerPhone());
        if (request.street() != null && !request.street().isBlank()) order.setStreet(request.street());
        if (request.houseNumber() != null && request.houseNumber() > 0) order.setHouseNumber(request.houseNumber());
        if (request.postalCode() != null && !request.postalCode().isBlank()) order.setPostalCode(request.postalCode());
        if (request.city() != null && !request.city().isBlank()) order.setCity(request.city());
        if (request.additionalComments() != null && !request.additionalComments().isBlank()) {
            order.setAdditionalComments(request.additionalComments());
        }

        if (request.meals() != null) {
            order.getOrderMeals().clear();
            addMeals(order, request.meals().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> parseMealKey(entry.getKey()),
                        Map.Entry::getValue
                )));
        }

        if (request.beverages() != null && !request.beverages().isEmpty()) {
            order.getOrderBeverages().clear();
            addBeverages(order, request.beverages());
        }

        if (request.addons() != null && !request.addons().isEmpty()) {
            order.getOrderAddons().clear();
            addAddons(order, request.addons());
        }

        ordersRepository.save(order);

        return response;
    }

    private void addMeals(Order order, Map<MealKey, Map<Size, Integer>> mealQuantities) {
        mealQuantities.forEach((mealKey, sizeQuantities) -> {
            Optional<Meal> optionalMeal = mealRepository.findByName(mealKey.getMealName());

            if (optionalMeal.isEmpty()) return;

            Meal meal = optionalMeal.get();
            sizeQuantities.forEach((size, quantity) -> {
                if (quantity != null && quantity > 0) {
                    order.addMeal(meal, size, quantity);
                    order.getOrderMeals().stream()
                            .filter(orderMeal -> orderMeal.getMealName().equals(meal.getName()))
                            .forEach(orderMeal -> {
                                orderMeal.getIngredientNames().add(mealKey.getMeat().getName());
                                orderMeal.getIngredientNames().add(mealKey.getSauce().getName());
                            });
                }
            });
        });
    }

    private MealKey parseMealKey(String key) {
        String[] parts = key.split("_");

        if (parts.length != 3) throw new InvalidMealKeyFormatException(key);

        Ingredient meat = ingredientRepository.findByName(parts[1])
            .orElseThrow(() -> new IngredientNotFoundException(parts[1]));
        Ingredient sauce = ingredientRepository.findByName(parts[2])
            .orElseThrow(() -> new IngredientNotFoundException(parts[2]));

        return MealKey.builder()
            .mealName(parts[0])
            .meat(meat)
            .sauce(sauce)
            .build();
    }

    private void addBeverages(Order order, Map<String, Map<BigDecimal, Integer>> beverageQuantities) {
        for (Map.Entry<String, Map<BigDecimal, Integer>> entry : beverageQuantities.entrySet()) {
            String beverageName = entry.getKey();
            Map<BigDecimal, Integer> capacityQuantities = entry.getValue();

            for (Map.Entry<BigDecimal, Integer> capacityEntry : capacityQuantities.entrySet()) {
                BigDecimal capacity = capacityEntry.getKey();
                Integer quantity = capacityEntry.getValue();

                if (quantity != null && quantity > 0) {
                    beverageRepository.findByNameAndCapacity(beverageName, capacity).ifPresent(
                beverage -> order.addBeverage(beverage, quantity));
                }
            }
        }
    }

    private void addAddons(Order order, Map<String, Integer> addonQuantities) {
        List<Addon> addons = addonRepository.findAllByNameIn(addonQuantities.keySet());
        for (Addon addon : addons) {
            Integer quantity = addonQuantities.get(addon.getName());
            if (quantity != null && quantity > 0) {
                order.addAddon(addon, quantity);
            }
        }
    }

    public RemovedOrderResponse removeOrder(Order order) {
        log.info("Started removing order with id '{}'", order.getId());

        ordersRepository.delete(order);

        RemovedOrderResponse response = RemovedOrderResponse.builder()
            .statusCode(HttpStatus.OK.value())
            .message("Successfully removed order with od '" + order.getId() + "'")
            .build();

        log.info("Successfully removed order with id '{}'", order.getId());

        return response;
    }
}
