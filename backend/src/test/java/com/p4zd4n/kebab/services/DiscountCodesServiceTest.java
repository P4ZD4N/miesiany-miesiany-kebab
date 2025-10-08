package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.DiscountCode;
import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.alreadyexists.DiscountCodeAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.alreadyexists.JobOfferAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.expired.DiscountCodeExpiredException;
import com.p4zd4n.kebab.exceptions.notfound.DiscountCodeNotFoundException;
import com.p4zd4n.kebab.repositories.DiscountCodesRepository;
import com.p4zd4n.kebab.requests.discountcodes.NewDiscountCodeRequest;
import com.p4zd4n.kebab.requests.discountcodes.UpdatedDiscountCodeRequest;
import com.p4zd4n.kebab.requests.jobs.UpdatedJobOfferRequest;
import com.p4zd4n.kebab.responses.discountcodes.DiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.NewDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.RemovedDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.UpdatedDiscountCodeResponse;
import com.p4zd4n.kebab.responses.jobs.RemovedJobOfferResponse;
import com.p4zd4n.kebab.responses.jobs.UpdatedJobOfferResponse;
import com.p4zd4n.kebab.services.discountcodes.DiscountCodesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscountCodesServiceTest {

  private final String exampleCode1 = "kodzik1";
  private final String exampleCode2 = "kodzik2";

  @Mock private DiscountCodesRepository discountCodesRepository;

  @InjectMocks private DiscountCodesService discountCodesService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getDiscountCodes_ShouldReturnDiscountCodes_WhenCalled() {

    List<DiscountCode> discountCodes =
        Arrays.asList(
            new DiscountCode(
                exampleCode1, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L),
            new DiscountCode(
                exampleCode2, BigDecimal.valueOf(30), LocalDate.now().plusMonths(1), 1L));

    when(discountCodesRepository.findAll()).thenReturn(discountCodes);

    List<DiscountCodeResponse> result = discountCodesService.getDiscountCodes();

    assertEquals(2, result.size());

    assertEquals(exampleCode1, result.getFirst().code());
    assertEquals(LocalDate.now().plusMonths(1), result.getFirst().expirationDate());

    assertEquals(exampleCode2, result.getLast().code());
    assertEquals(1, result.getLast().remainingUses());

    verify(discountCodesRepository, times(1)).findAll();
  }

  @Test
  public void getDiscountCode_ShouldReturnDiscountCode_WhenDiscountCodeExists() {

    DiscountCode discountCode =
        new DiscountCode(exampleCode1, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);

    when(discountCodesRepository.findByCode(exampleCode1)).thenReturn(Optional.of(discountCode));

    DiscountCodeResponse response = discountCodesService.getDiscountCode(exampleCode1);

    assertEquals(exampleCode1, response.code());
    assertEquals(LocalDate.now().plusMonths(1), response.expirationDate());
    assertEquals(3, response.remainingUses());

    verify(discountCodesRepository, times(1)).findByCode(exampleCode1);
  }

  @Test
  public void getDiscountCode_ShouldThrowDiscountCodeNotFoundException_WhenDiscountCodeNotExists() {

    when(discountCodesRepository.findByCode(exampleCode1)).thenReturn(Optional.empty());

    assertThrows(
        DiscountCodeNotFoundException.class,
        () -> {
          discountCodesService.getDiscountCode(exampleCode1);
        });

    verify(discountCodesRepository, times(1)).findByCode(exampleCode1);
  }

  @Test
  public void getDiscountCode_ShouldThrowDiscountCodeExpiredException_WhenDiscountCodeExpired() {

    DiscountCode discountCode =
        new DiscountCode(exampleCode1, BigDecimal.valueOf(20), LocalDate.now().minusMonths(1), 3L);

    when(discountCodesRepository.findByCode(exampleCode1)).thenReturn(Optional.of(discountCode));

    assertThrows(
        DiscountCodeExpiredException.class,
        () -> {
          discountCodesService.getDiscountCode(exampleCode1);
        });

    verify(discountCodesRepository, times(1)).findByCode(exampleCode1);
  }

  @Test
  public void addDiscountCode_ShouldAddDiscountCode_WhenSameDiscountCodeDoesNotExist() {

    NewDiscountCodeRequest request =
        NewDiscountCodeRequest.builder()
            .code(exampleCode1)
            .discountPercentage(BigDecimal.valueOf(20))
            .expirationDate(LocalDate.now().plusMonths(1))
            .remainingUses(3L)
            .build();

    when(discountCodesRepository.findByCode(request.code())).thenReturn(Optional.empty());

    DiscountCode discountCode =
        new DiscountCode(
            request.code(),
            request.discountPercentage(),
            request.expirationDate(),
            request.remainingUses());

    when(discountCodesRepository.save(any(DiscountCode.class))).thenReturn(discountCode);

    NewDiscountCodeResponse response = discountCodesService.addDiscountCode(request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals(
        "Successfully added new discount code: '" + exampleCode1 + "'", response.message());

    verify(discountCodesRepository, times(1)).findByCode(request.code());
    verify(discountCodesRepository, times(1)).save(any(DiscountCode.class));
  }

  @Test
  public void
      addDiscountCode_ShouldThrowDiscountCodeAlreadyExistsException_WhenSameDiscountCodeExists() {

    NewDiscountCodeRequest request =
        NewDiscountCodeRequest.builder()
            .code(exampleCode1)
            .discountPercentage(BigDecimal.valueOf(20))
            .expirationDate(LocalDate.now().plusMonths(1))
            .remainingUses(3L)
            .build();

    DiscountCode discountCode =
        new DiscountCode(
            request.code(),
            request.discountPercentage(),
            request.expirationDate(),
            request.remainingUses());

    when(discountCodesRepository.findByCode(request.code())).thenReturn(Optional.of(discountCode));

    assertThrows(
        DiscountCodeAlreadyExistsException.class,
        () -> {
          discountCodesService.addDiscountCode(request);
        });

    verify(discountCodesRepository, times(1)).findByCode(request.code());
  }

  @Test
  public void findDiscountCodeByCode_ShouldReturnDiscountCode_WhenDiscountCodeExists() {

    DiscountCode discountCode =
        new DiscountCode(exampleCode1, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);

    when(discountCodesRepository.findByCode(exampleCode1)).thenReturn(Optional.of(discountCode));

    DiscountCode foundDiscountCode = discountCodesService.findDiscountCodeByCode(exampleCode1);

    assertNotNull(foundDiscountCode);
    assertEquals("kodzik1", foundDiscountCode.getCode());
    assertEquals(BigDecimal.valueOf(20), foundDiscountCode.getDiscountPercentage());

    verify(discountCodesRepository, times(1)).findByCode(exampleCode1);
  }

  @Test
  public void
      findDiscountCodeByCode_ShouldThrowDiscountCodeNotFoundException_WhenDiscountCodeDoesNotExist() {

    when(discountCodesRepository.findByCode(exampleCode1))
        .thenThrow(new DiscountCodeNotFoundException(exampleCode1));

    DiscountCodeNotFoundException exception =
        assertThrows(
            DiscountCodeNotFoundException.class,
            () -> {
              discountCodesService.findDiscountCodeByCode(exampleCode1);
            });

    assertEquals("Discount code '" + exampleCode1 + "' not found!", exception.getMessage());

    verify(discountCodesRepository, times(1)).findByCode(exampleCode1);
  }

  @Test
  public void updateDiscountCode_ShouldUpdateDiscountCode_WhenCalled() {

    DiscountCode discountCode =
        new DiscountCode(exampleCode1, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);
    discountCode.setId(1L);

    UpdatedDiscountCodeRequest request =
        UpdatedDiscountCodeRequest.builder().code(exampleCode1).newCode(exampleCode2).build();

    when(discountCodesRepository.findByCode(request.newCode()))
        .thenReturn(Optional.of(discountCode));
    when(discountCodesRepository.save(any(DiscountCode.class))).thenReturn(discountCode);

    UpdatedDiscountCodeResponse response =
        discountCodesService.updateDiscountCode(discountCode, request);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully updated discount code 'kodzik1'", response.message());
    assertEquals("kodzik2", discountCode.getCode());

    verify(discountCodesRepository, times(1)).findByCode(discountCode.getCode());
    verify(discountCodesRepository, times(1)).save(discountCode);
  }

  @Test
  public void
      updateDiscountCode_ShouldThrowDiscountCodeAlreadyExistsException_WhenDiscountCodeExists() {

    DiscountCode discountCode1 =
        new DiscountCode(exampleCode1, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);
    discountCode1.setId(1L);

    DiscountCode discountCode2 =
        new DiscountCode(exampleCode2, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);
    discountCode2.setId(2L);

    UpdatedDiscountCodeRequest request =
        UpdatedDiscountCodeRequest.builder().code(exampleCode2).newCode(exampleCode1).build();

    when(discountCodesRepository.findByCode(request.newCode()))
        .thenReturn(Optional.of(discountCode1));

    assertThrows(
        DiscountCodeAlreadyExistsException.class,
        () -> {
          discountCodesService.updateDiscountCode(discountCode2, request);
        });

    verify(discountCodesRepository, times(1)).findByCode(request.newCode());
  }

  @Test
  public void removeDiscountCode_ShouldRemoveDiscountCode_WhenCalled() {

    DiscountCode discountCode =
        new DiscountCode(exampleCode1, BigDecimal.valueOf(20), LocalDate.now().plusMonths(1), 3L);

    doNothing().when(discountCodesRepository).delete(discountCode);

    RemovedDiscountCodeResponse response = discountCodesService.removeDiscountCode(discountCode);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals("Successfully removed discount code 'kodzik1'", response.message());

    verify(discountCodesRepository, times(1)).delete(discountCode);
  }
}
