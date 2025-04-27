package com.p4zd4n.kebab.services.discountcodes;

import com.p4zd4n.kebab.entities.DiscountCode;
import com.p4zd4n.kebab.exceptions.alreadyexists.DiscountCodeAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.DiscountCodeNotFoundException;
import com.p4zd4n.kebab.repositories.DiscountCodesRepository;
import com.p4zd4n.kebab.requests.discountcodes.NewDiscountCodeRequest;
import com.p4zd4n.kebab.requests.discountcodes.UpdatedDiscountCodeRequest;
import com.p4zd4n.kebab.responses.discountcodes.DiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.NewDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.RemovedDiscountCodeResponse;
import com.p4zd4n.kebab.responses.discountcodes.UpdatedDiscountCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiscountCodesService {

    private final DiscountCodesRepository discountCodesRepository;

    public DiscountCodesService(DiscountCodesRepository discountCodesRepository) {
        this.discountCodesRepository = discountCodesRepository;
    }

    public List<DiscountCodeResponse> getDiscountCodes() {

        log.info("Started retrieving discount codes");

        List<DiscountCode> discountCodes = discountCodesRepository.findAll();

        List<DiscountCodeResponse> response = discountCodes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved discount codes");

        return response;
    }

    public DiscountCodeResponse mapToResponse(DiscountCode discountCode) {

        return DiscountCodeResponse.builder()
                .id(discountCode.getId())
                .code(discountCode.getCode())
                .discountPercentage(discountCode.getDiscountPercentage())
                .expirationDate(discountCode.getExpirationDate())
                .build();
    }

    public DiscountCodeResponse getDiscountCode(String code) {

        log.info("Started retrieving discount code: '{}'", code);

        DiscountCode discountCode = discountCodesRepository.findByCode(code)
                .orElseThrow(() -> new DiscountCodeNotFoundException(code));

        DiscountCodeResponse response = mapToResponse(discountCode);

        log.info("Successfully retrieved discount code: '{}'", code);

        return response;
    }

    public NewDiscountCodeResponse addDiscountCode(NewDiscountCodeRequest request) {

        if (request.code() != null) {
            Optional<DiscountCode> existingCode = discountCodesRepository.findByCode(request.code());
            if (existingCode.isPresent()) throw new DiscountCodeAlreadyExistsException(request.code());
        }

        DiscountCode newDiscountCode = getDiscountCode(request);
        discountCodesRepository.save(newDiscountCode);
        String codeToDisplay = request.code() != null ? request.code() : newDiscountCode.getCode();

        return NewDiscountCodeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new discount code: '" + codeToDisplay + "'")
                .build();
    }

    private DiscountCode getDiscountCode(NewDiscountCodeRequest request) {

        DiscountCode newDiscountCode;
        if (request.code() == null && request.expirationDate() == null) {
            newDiscountCode = new DiscountCode(request.discountPercentage());
        } else if (request.code() != null && request.expirationDate() == null) {
            newDiscountCode = new DiscountCode(request.code(), request.discountPercentage());
        } else if (request.code() == null && request.expirationDate() != null) {
            newDiscountCode = new DiscountCode(request.discountPercentage(), request.expirationDate());
        } else {
            newDiscountCode = new DiscountCode(request.code(), request.discountPercentage(), request.expirationDate());
        }

        return newDiscountCode;
    }

    public DiscountCode findDiscountCodeByCode(String code) {

        log.info("Started finding discount code '{}'", code);

        DiscountCode discountCode = discountCodesRepository.findByCode(code)
                .orElseThrow(() -> new DiscountCodeNotFoundException(code));

        log.info("Successfully found discount code '{}'", code);

        return discountCode;
    }

    public UpdatedDiscountCodeResponse updateDiscountCode(DiscountCode discountCode, UpdatedDiscountCodeRequest request) {

        UpdatedDiscountCodeResponse response = UpdatedDiscountCodeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated discount code '" + discountCode.getCode() + "'")
                .build();

        if (request.newCode() != null) discountCode.setCode(request.newCode());
        if (request.discountPercentage() != null) discountCode.setDiscountPercentage(request.discountPercentage());
        if (request.expirationDate() != null) discountCode.setExpirationDate(request.expirationDate());

        discountCodesRepository.save(discountCode);

        return response;
    }

    public RemovedDiscountCodeResponse removeDiscountCode(DiscountCode discountCode) {

        log.info("Started removing discount code '{}'", discountCode.getCode());

        discountCodesRepository.delete(discountCode);

        RemovedDiscountCodeResponse response = RemovedDiscountCodeResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed discount code '" + discountCode.getCode() + "'")
                .build();

        log.info("Successfully removed discount code '{}'", discountCode.getCode());

        return response;
    }
}
