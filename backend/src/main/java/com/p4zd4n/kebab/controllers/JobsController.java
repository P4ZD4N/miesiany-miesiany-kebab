package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.jobs.JobOfferManagerResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferGeneralResponse;
import com.p4zd4n.kebab.services.jobs.JobOfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
