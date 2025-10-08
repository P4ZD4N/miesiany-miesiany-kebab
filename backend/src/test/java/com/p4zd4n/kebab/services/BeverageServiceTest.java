package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.exceptions.alreadyexists.BeverageAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.BeverageNotFoundException;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.requests.menu.beverages.NewBeverageRequest;
import com.p4zd4n.kebab.requests.menu.beverages.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.menu.beverages.BeverageResponse;
import com.p4zd4n.kebab.responses.menu.beverages.NewBeverageResponse;
import com.p4zd4n.kebab.responses.menu.beverages.RemovedBeverageResponse;
import com.p4zd4n.kebab.responses.menu.beverages.UpdatedBeverageResponse;
import com.p4zd4n.kebab.services.menu.BeverageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BeverageServiceTest {

  @Mock private BeverageRepository beverageRepository;

  @InjectMocks private BeverageService beverageService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getBeverages_ShouldReturnBeverages_WhenCalled() {

    List<Beverage> beverages =
        Arrays.asList(
            Beverage.builder()
                .name("Coca-Cola")
                .capacity(BigDecimal.valueOf(0.5))
                .price(BigDecimal.valueOf(5))
                .build(),
            Beverage.builder()
                .name("Fanta")
                .capacity(BigDecimal.valueOf(0.33))
                .price(BigDecimal.valueOf(4))
                .build(),
            Beverage.builder()
                .name("Sprite")
                .capacity(BigDecimal.valueOf(1))
                .price(BigDecimal.valueOf(8))
                .build());

    when(beverageRepository.findAll()).thenReturn(beverages);

    List<BeverageResponse> result = beverageService.getBeverages();

    assertEquals(3, result.size());

    assertEquals("Coca-Cola", result.getFirst().name());
    assertEquals(BigDecimal.valueOf(0.5), result.getFirst().capacity());
    assertEquals(BigDecimal.valueOf(5), result.getFirst().price());

    assertEquals("Sprite", result.getLast().name());
    assertEquals(BigDecimal.valueOf(1), result.getLast().capacity());
    assertEquals(BigDecimal.valueOf(8), result.getLast().price());

    verify(beverageRepository, times(1)).findAll();
  }

  @Test
  public void findBeverageByNameAndCapacity_ShouldReturnBeverage_WhenCalled() {

    Beverage beverage =
        Beverage.builder()
            .name("Coca-Cola")
            .capacity(BigDecimal.valueOf(0.5))
            .price(BigDecimal.valueOf(5))
            .build();

    when(beverageRepository.findByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.5)))
        .thenReturn(Optional.of(beverage));

    Beverage foundBeverage =
        beverageService.findBeverageByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.5));

    assertNotNull(foundBeverage);
    assertEquals("Coca-Cola", foundBeverage.getName());
    assertEquals(BigDecimal.valueOf(0.5), foundBeverage.getCapacity());
    assertEquals(BigDecimal.valueOf(5), foundBeverage.getPrice());

    verify(beverageRepository, times(1))
        .findByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.5));
  }

  @Test
  public void
      findBeverageByNameAndCapacity_ShouldThrowBeverageNotFoundException_WhenBeverageDoesNotExist() {

    when(beverageRepository.findByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.5)))
        .thenThrow(new BeverageNotFoundException("Coca-Cola"));

    BeverageNotFoundException exception =
        assertThrows(
            BeverageNotFoundException.class,
            () -> {
              beverageService.findBeverageByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.5));
            });

    assertEquals("Beverage with name 'Coca-Cola' not found!", exception.getMessage());

    verify(beverageRepository, times(1))
        .findByNameAndCapacity("Coca-Cola", BigDecimal.valueOf(0.5));
  }

  @Test
  public void addBeverage_ShouldAddBeverage_WhenBeverageDoesNotExist() {

    NewBeverageRequest request =
        NewBeverageRequest.builder()
            .newBeverageName("Fanta")
            .newBeverageCapacity(BigDecimal.valueOf(0.33))
            .newBeveragePrice(BigDecimal.valueOf(4))
            .build();

    when(beverageRepository.findByCapacity(request.newBeverageCapacity()))
        .thenReturn(Optional.of(Collections.emptyList()));

    Beverage newBeverage =
        Beverage.builder()
            .name(request.newBeverageName())
            .capacity(request.newBeverageCapacity())
            .price(request.newBeveragePrice())
            .build();

    when(beverageRepository.save(any(Beverage.class))).thenReturn(newBeverage);

    NewBeverageResponse response = beverageService.addBeverage(request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully added new beverage with name 'Fanta'", response.message());

    verify(beverageRepository, times(1)).findByCapacity(request.newBeverageCapacity());
    verify(beverageRepository, times(1)).save(any(Beverage.class));
  }

  @Test
  public void addBeverage_ShouldThrowBeverageAlreadyExistsException_WhenBeverageExists() {

    NewBeverageRequest request =
        NewBeverageRequest.builder()
            .newBeverageName("Fanta")
            .newBeverageCapacity(BigDecimal.valueOf(0.33))
            .newBeveragePrice(BigDecimal.valueOf(4))
            .build();

    Beverage existingBeverage =
        Beverage.builder()
            .name("Fanta")
            .capacity(BigDecimal.valueOf(0.33))
            .price(BigDecimal.valueOf(7))
            .build();

    when(beverageRepository.findByCapacity(request.newBeverageCapacity()))
        .thenReturn(Optional.of(Collections.singletonList(existingBeverage)));

    assertThrows(
        BeverageAlreadyExistsException.class,
        () -> {
          beverageService.addBeverage(request);
        });

    verify(beverageRepository, times(1)).findByCapacity(request.newBeverageCapacity());
  }

  @Test
  public void updateBeverage_ShouldUpdateBeverage_WhenBeverageDoesNotExist() {

    Beverage beverage =
        Beverage.builder()
            .name("Fanta")
            .capacity(BigDecimal.valueOf(0.33))
            .price(BigDecimal.valueOf(4))
            .build();

    UpdatedBeverageRequest request =
        UpdatedBeverageRequest.builder()
            .updatedBeverageName("Fanta")
            .updatedBeverageOldCapacity(BigDecimal.valueOf(0.33))
            .updatedBeverageNewCapacity(BigDecimal.valueOf(0.5))
            .updatedBeveragePrice(BigDecimal.valueOf(4))
            .build();

    when(beverageRepository.findByCapacity(request.updatedBeverageNewCapacity()))
        .thenReturn(Optional.of(Collections.emptyList()));
    when(beverageRepository.save(any(Beverage.class))).thenReturn(beverage);

    UpdatedBeverageResponse response = beverageService.updateBeverage(beverage, request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully updated beverage with name 'Fanta'", response.message());

    verify(beverageRepository, times(1)).findByCapacity(request.updatedBeverageNewCapacity());
    verify(beverageRepository, times(1)).save(beverage);
  }

  @Test
  public void updateBeverage_ShouldThrowBeverageAlreadyExistsException_WhenBeverageExists() {

    Beverage existingBeverage =
        Beverage.builder()
            .name("Fanta")
            .capacity(BigDecimal.valueOf(0.33))
            .price(BigDecimal.valueOf(4))
            .build();

    existingBeverage.setId(1L);

    Beverage beverageToUpdate =
        Beverage.builder()
            .name("Fanta")
            .capacity(BigDecimal.valueOf(0.5))
            .price(BigDecimal.valueOf(4))
            .build();

    beverageToUpdate.setId(2L);

    UpdatedBeverageRequest request =
        UpdatedBeverageRequest.builder()
            .updatedBeverageName("Fanta")
            .updatedBeverageOldCapacity(BigDecimal.valueOf(0.5))
            .updatedBeverageNewCapacity(BigDecimal.valueOf(0.33))
            .updatedBeveragePrice(BigDecimal.valueOf(4))
            .build();

    when(beverageRepository.findByCapacity(request.updatedBeverageNewCapacity()))
        .thenReturn(Optional.of(Collections.singletonList(existingBeverage)));

    assertThrows(
        BeverageAlreadyExistsException.class,
        () -> {
          beverageService.updateBeverage(beverageToUpdate, request);
        });

    verify(beverageRepository, times(1)).findByCapacity(request.updatedBeverageNewCapacity());
  }

  @Test
  public void removeBeverage_ShouldRemoveBeverage_WhenCalled() {

    Beverage beverage =
        Beverage.builder()
            .name("Fanta")
            .capacity(BigDecimal.valueOf(0.33))
            .price(BigDecimal.valueOf(4))
            .build();

    doNothing().when(beverageRepository).delete(beverage);

    RemovedBeverageResponse response = beverageService.removeBeverage(beverage);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully removed beverage with name 'Fanta'", response.message());

    verify(beverageRepository, times(1)).delete(beverage);
  }
}
