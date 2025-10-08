package com.p4zd4n.kebab.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.p4zd4n.kebab.entities.MealPromotion;
import com.p4zd4n.kebab.enums.Size;
import com.p4zd4n.kebab.exceptions.notfound.MealPromotionNotFoundException;
import com.p4zd4n.kebab.repositories.MealPromotionsRepository;
import com.p4zd4n.kebab.repositories.MealRepository;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.NewMealPromotionRequest;
import com.p4zd4n.kebab.requests.promotions.mealpromotions.UpdatedMealPromotionRequest;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.MealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.NewMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.RemovedMealPromotionResponse;
import com.p4zd4n.kebab.responses.promotions.mealpromotions.UpdatedMealPromotionResponse;
import com.p4zd4n.kebab.services.promotions.MealPromotionsService;
import jakarta.mail.MessagingException;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class MealPromotionsServiceTest {

  @Mock private MealPromotionsRepository mealPromotionsRepository;

  @Mock private MealRepository mealRepository;

  @InjectMocks private MealPromotionsService mealPromotionsService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getMealPromotions_ShouldReturnMealPromotions_WhenCalled() {

    List<MealPromotion> mealPromotionsList =
        Arrays.asList(
            MealPromotion.builder()
                .description("Large -20%")
                .sizes(Set.of(Size.LARGE))
                .discountPercentage(BigDecimal.valueOf(20))
                .build(),
            MealPromotion.builder()
                .description("Small and medium -10%")
                .sizes(Set.of(Size.SMALL, Size.MEDIUM))
                .discountPercentage(BigDecimal.valueOf(10))
                .build());

    when(mealPromotionsRepository.findAll()).thenReturn(mealPromotionsList);

    List<MealPromotionResponse> result = mealPromotionsService.getMealPromotions();

    assertEquals(2, result.size());

    assertEquals("Large -20%", result.getFirst().description());
    assertEquals(BigDecimal.valueOf(10), result.getLast().discountPercentage());

    verify(mealPromotionsRepository, times(1)).findAll();
  }

  @Test
  public void addMealPromotion_ShouldAddMeal_WhenValidRequest() throws MessagingException {

    NewMealPromotionRequest request =
        NewMealPromotionRequest.builder()
            .description("-10%")
            .sizes(Set.of(Size.SMALL))
            .discountPercentage(BigDecimal.valueOf(10))
            .mealNames(List.of("Pita"))
            .build();

    MealPromotion newMealPromotion =
        MealPromotion.builder()
            .description(request.description())
            .sizes(request.sizes())
            .discountPercentage(request.discountPercentage())
            .build();
    newMealPromotion.setId(1L);

    when(mealPromotionsRepository.save(any(MealPromotion.class))).thenReturn(newMealPromotion);

    NewMealPromotionResponse response = mealPromotionsService.addMealPromotion(request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully added new meal promotion with id '1'", response.message());

    verify(mealPromotionsRepository, times(1)).save(any(MealPromotion.class));
  }

  @Test
  public void findMealPromotionById_ShouldReturnMealPromotion_IfExists() {

    MealPromotion mealPromotion =
        MealPromotion.builder()
            .description("Large -20%")
            .sizes(Set.of(Size.LARGE))
            .discountPercentage(BigDecimal.valueOf(20))
            .build();

    when(mealPromotionsRepository.findById(1L)).thenReturn(Optional.of(mealPromotion));

    MealPromotion foundMealPromotion = mealPromotionsService.findMealPromotionById(1L);

    assertNotNull(foundMealPromotion);
    assertEquals("Large -20%", foundMealPromotion.getDescription());
    assertEquals(1, foundMealPromotion.getSizes().size());

    verify(mealPromotionsRepository, times(1)).findById(1L);
  }

  @Test
  public void findMealPromotionById_ShouldThrowMealPromotionsNotFoundException_IfDoesNotExists() {

    when(mealPromotionsRepository.findById(100L))
        .thenThrow(new MealPromotionNotFoundException(100L));

    MealPromotionNotFoundException exception =
        assertThrows(
            MealPromotionNotFoundException.class,
            () -> {
              mealPromotionsService.findMealPromotionById(100L);
            });

    assertEquals("Meal promotion with id '100' not found!", exception.getMessage());

    verify(mealPromotionsRepository, times(1)).findById(100L);
  }

  @Test
  public void updateMealPromotion_ShouldUpdateMealPromotion_WhenValidRequest() {

    MealPromotion mealPromotion =
        MealPromotion.builder()
            .description("Large -20%")
            .sizes(Set.of(Size.LARGE))
            .discountPercentage(BigDecimal.valueOf(20))
            .build();
    mealPromotion.setId(1L);

    UpdatedMealPromotionRequest request =
        UpdatedMealPromotionRequest.builder().id(1L).updatedDescription("Siema").build();

    when(mealPromotionsRepository.save(any(MealPromotion.class))).thenReturn(mealPromotion);

    UpdatedMealPromotionResponse response =
        mealPromotionsService.updateMealPromotion(mealPromotion, request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully updated meal promotion with id '1'", response.message());
    assertEquals("Siema", mealPromotion.getDescription());

    verify(mealPromotionsRepository, times(1)).save(mealPromotion);
  }

  @Test
  public void removeMealPromotion_ShouldRemoveMealPromotion_WhenValidRequest() {

    MealPromotion mealPromotion =
        MealPromotion.builder()
            .description("Large -20%")
            .sizes(Set.of(Size.LARGE))
            .discountPercentage(BigDecimal.valueOf(20))
            .build();
    mealPromotion.setId(2L);

    doNothing().when(mealPromotionsRepository).delete(mealPromotion);

    RemovedMealPromotionResponse response =
        mealPromotionsService.removeMealPromotion(mealPromotion);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully removed meal promotion with id '2'", response.message());
  }
}
