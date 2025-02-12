package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.repositories.AddonPromotionsRepository;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.NewAddonPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.AddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.NewAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.BeveragePromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.NewBeveragePromotionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddonPromotionsService {

    private final AddonRepository addonRepository;
    private final AddonPromotionsRepository addonPromotionsRepository;

    public AddonPromotionsService(AddonRepository addonRepository, AddonPromotionsRepository addonPromotionsRepository) {
        this.addonRepository = addonRepository;
        this.addonPromotionsRepository = addonPromotionsRepository;
    }

    public List<AddonPromotionResponse> getAddonPromotions() {

        log.info("Started retrieving addon promotions");

        List<AddonPromotion> promotions = addonPromotionsRepository.findAll();

        List<AddonPromotionResponse> response = promotions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved addon promotions");

        return response;
    }

    public AddonPromotionResponse mapToResponse(AddonPromotion addonPromotion) {

        Set<String> addonNames = addonPromotion.getAddons().stream()
                .map(Addon::getName)
                .collect(Collectors.toSet());

        return AddonPromotionResponse.builder()
                .description(addonPromotion.getDescription())
                .discountPercentage(addonPromotion.getDiscountPercentage())
                .addonNames(addonNames)
                .build();
    }

    public NewAddonPromotionResponse addAddonPromotion(NewAddonPromotionRequest request) {

        AddonPromotion addonPromotion = AddonPromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();

        AddonPromotion savedAddonPromotion = addonPromotionsRepository.save(addonPromotion);

        if (request.addonNames() != null)
            addonRepository.findAll().stream()
                    .filter(addon -> request.addonNames().contains(addon.getName()))
                    .forEach(addon -> {
                        addon.setPromotion(addonPromotion);
                        addonRepository.save(addon);
                    });

        return NewAddonPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new addon promotion with id '" + savedAddonPromotion.getId() + "'")
                .build();
    }
}
