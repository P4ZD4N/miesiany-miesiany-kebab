package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.AddonPromotion;
import com.p4zd4n.kebab.entities.BeveragePromotion;
import com.p4zd4n.kebab.repositories.AddonPromotionsRepository;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.requests.promotions.addonpromotions.NewAddonPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.beveragepromotions.NewBeveragePromotionRequest;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.AddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.NewAddonPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.beveragepromotions.NewBeveragePromotionResponse;
import com.p4zd4n.kebab.services.promotions.AddonPromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AddonPromotionsServiceTest {

    @Mock
    private AddonRepository addonRepository;

    @Mock
    private AddonPromotionsRepository addonPromotionsRepository;

    @InjectMocks
    private AddonPromotionsService addonPromotionsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAddonPromotions_ShouldReturnAddonPromotions_WhenCalled() {

        List<AddonPromotion> addonPromotionsList = Arrays.asList(
                AddonPromotion.builder()
                        .description("-20%")
                        .discountPercentage(BigDecimal.valueOf(20))
                        .build(),
                AddonPromotion.builder()
                        .description("-10%")
                        .discountPercentage(BigDecimal.valueOf(10))
                        .build()
        );

        when(addonPromotionsRepository.findAll()).thenReturn(addonPromotionsList);

        List<AddonPromotionResponse> result = addonPromotionsService.getAddonPromotions();

        assertEquals(2, result.size());
        assertEquals("-20%", result.getFirst().description());
        assertEquals(BigDecimal.valueOf(10), result.getLast().discountPercentage());

        verify(addonPromotionsRepository, times(1)).findAll();
    }

    @Test
    public void addAddonPromotion_ShouldAddAddonPromotion_WhenValidRequestWithNullSetInRequest() {

        NewAddonPromotionRequest request = NewAddonPromotionRequest.builder()
                .description("-10%")
                .discountPercentage(BigDecimal.valueOf(10))
                .build();

        AddonPromotion newAddonPromotion = AddonPromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();
        newAddonPromotion.setId(1L);

        when(addonPromotionsRepository.save(any(AddonPromotion.class))).thenReturn(newAddonPromotion);

        NewAddonPromotionResponse response = addonPromotionsService.addAddonPromotion(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new addon promotion with id '1'", response.message());

        verify(addonPromotionsRepository, times(1)).save(any(AddonPromotion.class));
    }

    @Test
    public void addAddonPromotion_ShouldAddAddonPromotion_WhenValidRequestWithNotNullSetInRequest() {

        NewAddonPromotionRequest request = NewAddonPromotionRequest.builder()
                .description("-10%")
                .discountPercentage(BigDecimal.valueOf(10))
                .addonNames(Set.of("Jalapeno"))
                .build();

        AddonPromotion newAddonPromotion = AddonPromotion.builder()
                .description(request.description())
                .discountPercentage(request.discountPercentage())
                .build();
        newAddonPromotion.setId(1L);

        when(addonPromotionsRepository.save(any(AddonPromotion.class))).thenReturn(newAddonPromotion);

        NewAddonPromotionResponse response = addonPromotionsService.addAddonPromotion(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new addon promotion with id '1'", response.message());

        verify(addonPromotionsRepository, times(1)).save(any(AddonPromotion.class));
    }
}
