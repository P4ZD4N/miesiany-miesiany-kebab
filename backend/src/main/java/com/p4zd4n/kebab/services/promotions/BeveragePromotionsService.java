package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.repositories.BeveragePromotionsRepository;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BeveragePromotionsService {

    private final BeveragePromotionsRepository beveragePromotionsRepository;
    private final BeverageRepository beverageRepository;

    public BeveragePromotionsService(
        BeveragePromotionsRepository beveragePromotionsRepository,
        BeverageRepository beverageRepository
    ) {
        this.beveragePromotionsRepository = beveragePromotionsRepository;
        this.beverageRepository = beverageRepository;
    }

    public List<BeveragePromotionResponse> getBeveragePromotions() {

        log.info("Started retrieving beverage promotions");

        List<BeveragePromotion> promotions = beveragePromotionsRepository.findAll();

        List<BeveragePromotionResponse> response = promotions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved beverage promotions");

        return response;
    }

    public BeveragePromotionResponse mapToResponse(BeveragePromotion beveragePromotion) {

        Map<String, List<BigDecimal>> beveragesWithCapacities = beveragePromotion.getBeverages().stream()
                .collect(Collectors.groupingBy(
                        Beverage::getName,
                        Collectors.mapping(Beverage::getCapacity, Collectors.toList())
                ));

        return BeveragePromotionResponse.builder()
                .description(beveragePromotion.getDescription())
                .discountPercentage(beveragePromotion.getDiscountPercentage())
                .beveragesWithCapacities(beveragesWithCapacities)
                .build();
    }
}
