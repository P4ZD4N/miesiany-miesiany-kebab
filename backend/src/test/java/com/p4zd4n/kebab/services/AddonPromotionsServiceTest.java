package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.AddonPromotion;
import com.p4zd4n.kebab.repositories.AddonPromotionsRepository;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.responses.promotions.addonpromotions.AddonPromotionResponse;
import com.p4zd4n.kebab.services.promotions.AddonPromotionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
