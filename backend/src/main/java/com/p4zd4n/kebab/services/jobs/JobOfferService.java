package com.p4zd4n.kebab.services.jobs;

import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.repositories.JobOfferRepository;
import com.p4zd4n.kebab.responses.jobs.JobOfferGeneralResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferManagerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobOfferService {

    private final JobOfferRepository jobOfferRepository;

    public JobOfferService(JobOfferRepository jobOfferRepository) {
        this.jobOfferRepository = jobOfferRepository;
    }

    public List<JobOfferGeneralResponse> getJobOffersForOtherUsers() {

        log.info("Started retrieving job offers for other users");

        List<JobOffer> jobOffers = jobOfferRepository.findAll();

        List<JobOfferGeneralResponse> response = jobOffers.stream()
                .map(jobOffer -> JobOfferGeneralResponse.builder()
                        .positionName(jobOffer.getPositionName())
                        .description(jobOffer.getDescription())
                        .monthlySalary(jobOffer.getMonthlySalary())
                        .jobRequirements(jobOffer.getJobRequirements())
                        .build()
                )
                .collect(Collectors.toList());

        log.info("Successfully retrieved job offers for other users");

        return response;

    }

    public List<JobOfferManagerResponse> getJobOffersForManager() {

        log.info("Started retrieving job offers for manager");

        List<JobOffer> jobOffers = jobOfferRepository.findAll();

        List<JobOfferManagerResponse> response = jobOffers.stream()
                .map(jobOffer -> JobOfferManagerResponse.builder()
                        .positionName(jobOffer.getPositionName())
                        .description(jobOffer.getDescription())
                        .monthlySalary(jobOffer.getMonthlySalary())
                        .jobRequirements(jobOffer.getJobRequirements())
                        .jobApplications(jobOffer.getJobApplications())
                        .build()
                )
                .collect(Collectors.toList());

        log.info("Successfully retrieved job offers for manager");

        return response;
    }
}
