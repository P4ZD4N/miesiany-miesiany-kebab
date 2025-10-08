package com.p4zd4n.kebab.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.exceptions.alreadyexists.AddonAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.AddonNotFoundException;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.requests.menu.addons.NewAddonRequest;
import com.p4zd4n.kebab.requests.menu.addons.UpdatedAddonRequest;
import com.p4zd4n.kebab.responses.menu.addons.AddonResponse;
import com.p4zd4n.kebab.responses.menu.addons.NewAddonResponse;
import com.p4zd4n.kebab.responses.menu.addons.RemovedAddonResponse;
import com.p4zd4n.kebab.responses.menu.addons.UpdatedAddonResponse;
import com.p4zd4n.kebab.services.menu.AddonService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

public class AddonServiceTest {

  @Mock private AddonRepository addonRepository;

  @InjectMocks private AddonService addonService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getAddons_ShouldReturnAddons_WhenCalled() {

    List<Addon> addons =
        Arrays.asList(
            Addon.builder().name("Jalapeno").price(BigDecimal.valueOf(3)).build(),
            Addon.builder().name("Herbs").price(BigDecimal.valueOf(5)).build(),
            Addon.builder().name("Feta").price(BigDecimal.valueOf(4)).build());

    when(addonRepository.findAll()).thenReturn(addons);

    List<AddonResponse> result = addonService.getAddons();

    assertEquals(3, result.size());

    assertEquals("Jalapeno", result.getFirst().name());
    assertEquals(BigDecimal.valueOf(3), result.getFirst().price());

    assertEquals("Feta", result.getLast().name());
    assertEquals(BigDecimal.valueOf(4), result.getLast().price());

    verify(addonRepository, times(1)).findAll();
  }

  @Test
  public void findAddonByName_ShouldReturnAddon_WhenAddonExists() {

    Addon addon = Addon.builder().name("Feta").price(BigDecimal.valueOf(4)).build();

    when(addonRepository.findByName("Feta")).thenReturn(Optional.of(addon));

    Addon foundAddon = addonService.findAddonByName("Feta");

    assertNotNull(foundAddon);
    assertEquals("Feta", foundAddon.getName());
    assertEquals(BigDecimal.valueOf(4), foundAddon.getPrice());

    verify(addonRepository, times(1)).findByName("Feta");
  }

  @Test
  public void findAddonByName_ShouldThrowAddonNotFoundException_WhenAddonDoesNotExist() {

    when(addonRepository.findByName("Herbs")).thenThrow(new AddonNotFoundException("Herbs"));

    AddonNotFoundException exception =
        assertThrows(
            AddonNotFoundException.class,
            () -> {
              addonService.findAddonByName("Herbs");
            });

    assertEquals("Addon with name 'Herbs' not found!", exception.getMessage());

    verify(addonRepository, times(1)).findByName("Herbs");
  }

  @Test
  public void addAddon_ShouldAddAddon_WhenAddonDoesNotExist() {

    NewAddonRequest request =
        NewAddonRequest.builder().newAddonName("Feta").newAddonPrice(BigDecimal.valueOf(4)).build();

    when(addonRepository.findByName(request.newAddonName())).thenReturn(Optional.empty());

    Addon newAddon =
        Addon.builder().name(request.newAddonName()).price(request.newAddonPrice()).build();

    when(addonRepository.save(any(Addon.class))).thenReturn(newAddon);

    NewAddonResponse response = addonService.addAddon(request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully added new addon with name 'Feta'", response.message());

    verify(addonRepository, times(1)).findByName(request.newAddonName());
    verify(addonRepository, times(1)).save(any(Addon.class));
  }

  @Test
  public void addAddon_ShouldThrowAddonAlreadyExistsException_WhenAddonExists() {

    NewAddonRequest request =
        NewAddonRequest.builder().newAddonName("Feta").newAddonPrice(BigDecimal.valueOf(4)).build();

    Addon existingAddon = Addon.builder().name("Feta").price(BigDecimal.valueOf(7)).build();

    when(addonRepository.findByName(request.newAddonName())).thenReturn(Optional.of(existingAddon));

    assertThrows(
        AddonAlreadyExistsException.class,
        () -> {
          addonService.addAddon(request);
        });

    verify(addonRepository, times(1)).findByName(request.newAddonName());
  }

  @Test
  public void updateAddon_ShouldUpdateAddon_WhenCalled() {

    Addon addon = Addon.builder().name("Feta").price(BigDecimal.valueOf(7)).build();

    UpdatedAddonRequest request =
        UpdatedAddonRequest.builder()
            .updatedAddonName("Feta")
            .updatedAddonPrice(BigDecimal.valueOf(4))
            .build();

    when(addonRepository.findByName(request.updatedAddonName())).thenReturn(Optional.of(addon));
    when(addonRepository.save(any(Addon.class))).thenReturn(addon);

    UpdatedAddonResponse response = addonService.updateAddon(addon, request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully updated addon with name 'Feta'", response.message());

    verify(addonRepository, times(1)).save(addon);
  }

  @Test
  public void removeAddon_ShouldRemoveAddon_WhenCalled() {

    Addon addon = Addon.builder().name("Feta").price(BigDecimal.valueOf(7)).build();

    doNothing().when(addonRepository).delete(addon);

    RemovedAddonResponse response = addonService.removeAddon(addon);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully removed addon with name 'Feta'", response.message());

    verify(addonRepository, times(1)).delete(addon);
  }
}
