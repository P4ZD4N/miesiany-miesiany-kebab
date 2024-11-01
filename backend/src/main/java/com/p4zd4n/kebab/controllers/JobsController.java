package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.jobs.NewJobOfferRequest;
import com.p4zd4n.kebab.responses.jobs.JobOfferManagerResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferGeneralResponse;
import com.p4zd4n.kebab.responses.jobs.NewJobOfferResponse;
import com.p4zd4n.kebab.services.jobs.JobOfferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class JobsController {

    private final JobOfferService jobOfferService;

    public JobsController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @GetMapping("/job-offers/manager")
    public ResponseEntity<List<JobOfferManagerResponse>> getJobOffersForManager() {
        log.info("Received get job offers for manager request");

        return ResponseEntity.ok(jobOfferService.getJobOffersForManager());
    }

    @GetMapping("/job-offers/general")
    public ResponseEntity<List<JobOfferGeneralResponse>> getJobOffersForOtherUsers() {
        log.info("Received get job offers for other users request");

        return ResponseEntity.ok(jobOfferService.getJobOffersForOtherUsers());
    }

    @PostMapping("/add-job-offer")
    public ResponseEntity<NewJobOfferResponse> addJobOffer(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody NewJobOfferRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add job offer request");

        NewJobOfferResponse response = jobOfferService.addJobOffer(request);

        log.info("Successfully added new job offer: {}", request.positionName());

        return ResponseEntity.ok(response);
    }
}
