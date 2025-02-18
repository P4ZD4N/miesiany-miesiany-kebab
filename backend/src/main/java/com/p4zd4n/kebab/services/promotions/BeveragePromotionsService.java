package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.exceptions.notfound.BeveragePromotionNotFoundException;
import com.p4zd4n.kebab.repositories.BeveragePromotionsRepository;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.NewBeveragePromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.UpdatedBeveragePromotionRequest;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.NewBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.RemovedBeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.UpdatedBeveragePromotionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
                .id(beveragePromotion.getId())
                .description(beveragePromotion.getDescription())
                .discountPercentage(beveragePromotion.getDiscountPercentage())
                .beveragesWithCapacities(beveragesWithCapacities)
                .build();
    }

    public NewBeveragePromotionResponse addBeveragePromotion(NewBeveragePromotionRequest request) {

        BeveragePromotion beveragePromotion = BeveragePromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();

        BeveragePromotion savedBeveragePromotion = beveragePromotionsRepository.save(beveragePromotion);

        if (request.beveragesWithCapacities() != null)
            beverageRepository.findAll().stream()
                .filter(beverage -> {
                    return request.beveragesWithCapacities().containsKey(beverage.getName()) &&
                            request.beveragesWithCapacities().get(beverage.getName()).stream()
                                    .anyMatch(capacity -> capacity.compareTo(beverage.getCapacity()) == 0);
                })
                .forEach(beverage -> {
                    beverage.setPromotion(beveragePromotion);
                    beverageRepository.save(beverage);
                });

        return NewBeveragePromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new beverage promotion with id '" + savedBeveragePromotion.getId() + "'")
                .build();
    }

    public BeveragePromotion findBeveragePromotionById(Long id) {

        log.info("Started finding beverage promotion with id '{}'", id);

        BeveragePromotion beveragePromotion = beveragePromotionsRepository.findById(id)
                .orElseThrow(() -> new BeveragePromotionNotFoundException(id));

        log.info("Successfully found beverage promotion with id '{}'", id);

        return beveragePromotion;
    }

    public UpdatedBeveragePromotionResponse updateBeveragePromotion(
            BeveragePromotion beveragePromotion, UpdatedBeveragePromotionRequest request
    ) {
        UpdatedBeveragePromotionResponse response = UpdatedBeveragePromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated beverage promotion with id '" + beveragePromotion.getId() + "'")
                .build();

        if (request.updatedDescription() != null)
            beveragePromotion.setDescription(request.updatedDescription());

        if (request.updatedBeveragesWithCapacities() != null) {
            List<Beverage> updatedBeverages = beverageRepository.findAll().stream()
                    .filter(beverage -> {
                        return request.updatedBeveragesWithCapacities().containsKey(beverage.getName()) &&
                                request.updatedBeveragesWithCapacities().get(beverage.getName()).contains(beverage.getCapacity());
                    }).toList();
            List<Beverage> existingBeverages = new ArrayList<>(beveragePromotion.getBeverages());

            existingBeverages.forEach(beverage -> {
                beverage.setPromotion(null);
                beverageRepository.save(beverage);
            });

            updatedBeverages.forEach(beverage -> {
                beverage.setPromotion(beveragePromotion);
                beverageRepository.save(beverage);
            });

            beveragePromotion.getBeverages().clear();
            beveragePromotion.getBeverages().addAll(updatedBeverages);
        }

        if (request.updatedDiscountPercentage() != null)
            beveragePromotion.setDiscountPercentage(request.updatedDiscountPercentage());

        beveragePromotionsRepository.save(beveragePromotion);

        return response;
    }

    public RemovedBeveragePromotionResponse removeBeveragePromotion(BeveragePromotion beveragePromotion) {

        log.info("Started removing beverage promotion with id '{}'", beveragePromotion.getId());

        beveragePromotion.getBeverages().forEach(beverage -> {
            beverage.setPromotion(null);
            beverageRepository.save(beverage);
        });

        beveragePromotionsRepository.delete(beveragePromotion);

        RemovedBeveragePromotionResponse response = RemovedBeveragePromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed beverage promotion with id '" + beveragePromotion.getId() + "'")
                .build();

        log.info("Successfully removed beverage promotion with id '{}'", beveragePromotion.getId());

        return response;
    }
}
