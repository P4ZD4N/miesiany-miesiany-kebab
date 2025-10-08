package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.DiscountCode;
import com.p4zd4n.kebab.requests.discountcodes.NewDiscountCodeRequest;
import com.p4zd4n.kebab.requests.discountcodes.RemovedDiscountCodeRequest;
import com.p4zd4n.kebab.requests.discountcodes.UpdatedDiscountCodeRequest;
import com.p4zd4n.kebab.responses.discountcodes.DiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.NewDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.RemovedDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.UpdatedDiscountCodeResponse;
import com.p4zd4n.kebab.services.discountcodes.DiscountCodesService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/discount-codes")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class DiscountCodesController {

  private final DiscountCodesService discountCodesService;

  public DiscountCodesController(DiscountCodesService discountCodesService) {
    this.discountCodesService = discountCodesService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<DiscountCodeResponse>> getDiscountCodes() {
    log.info("Received get discount codes request");
    return ResponseEntity.ok(discountCodesService.getDiscountCodes());
  }

  @GetMapping("/{code}")
  public ResponseEntity<DiscountCodeResponse> getDiscountCode(@PathVariable String code) {
    log.info("Received get discount code request");
    return ResponseEntity.ok(discountCodesService.getDiscountCode(code));
  }

  @PostMapping("/add-discount-code")
  public ResponseEntity<NewDiscountCodeResponse> addDiscountCode(
      @RequestHeader(value = "Accept-Language") String language,
      @Valid @RequestBody NewDiscountCodeRequest request) {
    LanguageValidator.validateLanguage(language);

    log.info("Received add discount code request");

    NewDiscountCodeResponse response = discountCodesService.addDiscountCode(request);

    log.info("Successfully added new discount code");

    return ResponseEntity.ok(response);
  }

  @PutMapping("/update-discount-code")
  public ResponseEntity<UpdatedDiscountCodeResponse> updateDiscountCode(
      @RequestHeader(value = "Accept-Language") String language,
      @Valid @RequestBody UpdatedDiscountCodeRequest request) {
    LanguageValidator.validateLanguage(language);

    log.info("Received update discount code request");

    DiscountCode existingDiscountCode = discountCodesService.findDiscountCodeByCode(request.code());
    UpdatedDiscountCodeResponse response =
        discountCodesService.updateDiscountCode(existingDiscountCode, request);

    log.info("Successfully updated discount code");

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/remove-discount-code")
  public ResponseEntity<RemovedDiscountCodeResponse> removeDiscountCode(
      @Valid @RequestBody RemovedDiscountCodeRequest request) {
    log.info("Received remove discount code request");

    DiscountCode existingDiscountCode = discountCodesService.findDiscountCodeByCode(request.code());
    RemovedDiscountCodeResponse response =
        discountCodesService.removeDiscountCode(existingDiscountCode);

    return ResponseEntity.ok(response);
  }
}
