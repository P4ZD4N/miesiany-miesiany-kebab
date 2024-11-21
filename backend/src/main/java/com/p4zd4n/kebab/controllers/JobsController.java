package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.invalid.InvalidAcceptLanguageHeaderValue;
import com.p4zd4n.kebab.requests.jobs.*;
import com.p4zd4n.kebab.responses.jobs.*;
import com.p4zd4n.kebab.services.jobs.JobApplicationService;
import com.p4zd4n.kebab.services.jobs.JobOfferService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class JobsController {

    private final JobOfferService jobOfferService;
    private final JobApplicationService jobApplicationService;

    public JobsController(JobOfferService jobOfferService, JobApplicationService jobApplicationService) {
        this.jobOfferService = jobOfferService;
        this.jobApplicationService = jobApplicationService;
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

    @PutMapping("/update-job-offer")
    public ResponseEntity<UpdatedJobOfferResponse> updateJobOffer(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody UpdatedJobOfferRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received update job offer request");

        JobOffer existingJobOffer = jobOfferService.findJobOfferByPositionName(request.positionName());
        UpdatedJobOfferResponse response = jobOfferService.updateJobOffer(existingJobOffer, request);

        log.info("Successfully updated job offer: {}", request.updatedPositionName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-job-offer")
    public ResponseEntity<RemovedJobOfferResponse> removeAddon(
            @Valid @RequestBody RemovedJobOfferRequest request
    ) {
        log.info("Received remove job offer request");

        JobOffer existingJobOffer = jobOfferService.findJobOfferByPositionName(request.positionName());
        RemovedJobOfferResponse response = jobOfferService.removeJobOffer(existingJobOffer);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-job-offer-application")
    public ResponseEntity<JobOfferApplicationResponse> addJobApplication(
            @RequestHeader(value = "Accept-Language") String language,
            @Valid @RequestBody JobOfferApplicationRequest request
    ) {
        if (!language.equalsIgnoreCase("en") && !language.equalsIgnoreCase("pl")) {
            throw new InvalidAcceptLanguageHeaderValue(language);
        }

        log.info("Received add job offer application request");

        JobOfferApplicationResponse response = jobApplicationService.addJobOfferApplication(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-cv")
    public ResponseEntity<NewCvResponse> addCv(
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("cv") MultipartFile cv
    ) throws IOException {
        log.info("Received add cv request");

        NewCvResponse response = jobApplicationService.addCv(cv, applicationId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download-cv/{id}")
    public ResponseEntity<byte[]> downloadCv(@PathVariable Long id) {

        log.info("Received download cv request");

        ResponseEntity<byte[]> response = jobApplicationService.getCv(id);

        log.info("Successfully downloaded cv with id: {}", id);

        return response;
    }

    @DeleteMapping("/remove-application")
    public ResponseEntity<RemovedApplicationResponse> removeApplication(
            @Valid @RequestBody RemovedApplicationRequest request
    ) {
        log.info("Received remove applicant request");

        JobOffer existingJobOffer = jobOfferService.findJobOfferByPositionName(request.positionName());
        RemovedApplicationResponse response = jobApplicationService.removeApplication(request.applicationId(), existingJobOffer);

        return ResponseEntity.ok(response);
    }
}
