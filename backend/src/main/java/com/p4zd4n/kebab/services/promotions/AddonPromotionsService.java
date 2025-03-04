package com.p4zd4n.kebab.services.promotions;

import com.p4zd4n.kebab.entities.*;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import com.p4zd4n.kebab.exceptions.notfound.AddonPromotionNotFoundException;
import com.p4zd4n.kebab.repositories.AddonPromotionsRepository;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.repositories.NewsletterRepository;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.NewAddonPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.UpdatedAddonPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.AddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.NewAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.RemovedAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.UpdatedAddonPromotionResponse;

import com.p4zd4n.kebab.utils.mails.PromotionMailUtil;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddonPromotionsService {

    private final AddonRepository addonRepository;
    private final AddonPromotionsRepository addonPromotionsRepository;
    private final NewsletterRepository newsletterRepository;
    private final PromotionMailUtil promotionMailUtil;

    public AddonPromotionsService(
            AddonRepository addonRepository,
            AddonPromotionsRepository addonPromotionsRepository,
            NewsletterRepository newsletterRepository,
            PromotionMailUtil promotionMailUtil
    ) {
        this.addonRepository = addonRepository;
        this.addonPromotionsRepository = addonPromotionsRepository;
        this.newsletterRepository = newsletterRepository;
        this.promotionMailUtil = promotionMailUtil;
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
                .id(addonPromotion.getId())
                .description(addonPromotion.getDescription())
                .discountPercentage(addonPromotion.getDiscountPercentage())
                .addonNames(addonNames)
                .build();
    }

    public NewAddonPromotionResponse addAddonPromotion(NewAddonPromotionRequest request) throws MessagingException {

        AddonPromotion addonPromotion = AddonPromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();

        if (request.addonNames() != null) {
            List<Addon> addons = addonRepository.findAll().stream()
                    .filter(addon -> request.addonNames().contains(addon.getName()))
                    .toList();
            addonPromotion.getAddons().addAll(addons);
        }

        AddonPromotion savedAddonPromotion = addonPromotionsRepository.save(addonPromotion);

        if (request.addonNames() != null)
            addonRepository.findAll().stream()
                    .filter(addon -> request.addonNames().contains(addon.getName()))
                    .forEach(addon -> {
                        addon.setPromotion(addonPromotion);
                        addonRepository.save(addon);
                    });

        for (NewsletterSubscriber subscriber : newsletterRepository.findAllByIsActiveTrue()) {
            if (subscriber.getNewsletterMessagesLanguage().equals(NewsletterMessagesLanguage.ENGLISH))
                promotionMailUtil.sendEng(subscriber, savedAddonPromotion);
            else
                promotionMailUtil.sendPl(subscriber, savedAddonPromotion);
        }

        return NewAddonPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new addon promotion with id '" + savedAddonPromotion.getId() + "'")
                .build();
    }

    public AddonPromotion findAddonPromotionById(Long id) {

        log.info("Started finding addon promotion with id '{}'", id);

        AddonPromotion addonPromotion = addonPromotionsRepository.findById(id)
                .orElseThrow(() -> new AddonPromotionNotFoundException(id));

        log.info("Successfully found addon promotion with id '{}'", id);

        return addonPromotion;
    }

    public UpdatedAddonPromotionResponse updateAddonPromotion(
            AddonPromotion addonPromotion, UpdatedAddonPromotionRequest request
    ) {

        UpdatedAddonPromotionResponse response = UpdatedAddonPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated addon promotion with id '" + addonPromotion.getId() + "'")
                .build();

        if (request.updatedDescription() != null)
            addonPromotion.setDescription(request.updatedDescription());

        if (request.updatedDiscountPercentage() != null)
            addonPromotion.setDiscountPercentage(request.updatedDiscountPercentage());

        if (request.updatedAddonNames() != null) {
            List<Addon> updatedAddons = addonRepository.findAll().stream()
                    .filter(addon -> request.updatedAddonNames().contains(addon.getName()))
                    .toList();
            List<Addon> existingAddons = new ArrayList<>(addonPromotion.getAddons());

            existingAddons.forEach(addon -> {
                addon.setPromotion(null);
                addonRepository.save(addon);
            });

            updatedAddons.forEach(addon -> {
                addon.setPromotion(addonPromotion);
                addonRepository.save(addon);
            });

            addonPromotion.getAddons().clear();
            addonPromotion.getAddons().addAll(updatedAddons);
        }

        addonPromotionsRepository.save(addonPromotion);

        return response;
    }

    public RemovedAddonPromotionResponse removeAddonPromotion(AddonPromotion addonPromotion) {

        log.info("Started removing addon promotion with id '{}'", addonPromotion.getId());

        addonPromotion.getAddons().forEach(addon -> {
            addon.setPromotion(null);
            addonRepository.save(addon);
        });

        addonPromotionsRepository.delete(addonPromotion);

        RemovedAddonPromotionResponse response = RemovedAddonPromotionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed addon promotion with id '" + addonPromotion.getId() + "'")
                .build();

        log.info("Successfully removed addon promotion with id '{}'", addonPromotion.getId());

        return response;
    }
}
