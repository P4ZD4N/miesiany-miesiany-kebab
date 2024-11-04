package com.p4zd4n.kebab.services.jobs;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.alreadyexists.BeverageAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.alreadyexists.JobOfferAlreadyExistsException;
import com.p4zd4n.kebab.repositories.JobOfferRepository;
import com.p4zd4n.kebab.requests.jobs.NewJobOfferRequest;
import com.p4zd4n.kebab.requests.menu.beverages.NewBeverageRequest;
import com.p4zd4n.kebab.responses.jobs.JobOfferGeneralResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferManagerResponse;
import com.p4zd4n.kebab.responses.jobs.NewJobOfferResponse;
import com.p4zd4n.kebab.responses.menu.beverages.NewBeverageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
                        .hourlyWage(jobOffer.getHourlyWage())
                        .jobEmploymentTypes(jobOffer.getJobEmploymentTypes())
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
                        .hourlyWage(jobOffer.getHourlyWage())
                        .jobEmploymentTypes(jobOffer.getJobEmploymentTypes())
                        .jobRequirements(jobOffer.getJobRequirements())
                        .jobApplications(jobOffer.getJobApplications())
                        .build()
                )
                .collect(Collectors.toList());

        log.info("Successfully retrieved job offers for manager");

        return response;
    }

    public NewJobOfferResponse addJobOffer(NewJobOfferRequest request) {

        Optional<JobOffer> jobOffer = jobOfferRepository.findByPositionName(request.positionName());

        if (jobOffer.isPresent()) throw new JobOfferAlreadyExistsException(request.positionName());

        log.info("Started adding job offer with position name '{}'", request.positionName());

        JobOffer newJobOffer = JobOffer.builder()
                .positionName(request.positionName())
                .description(request.description())
                .hourlyWage(request.hourlyWage())
                .build();

        if (request.jobEmploymentTypes() != null && !request.jobEmploymentTypes().isEmpty()) {
            request.jobEmploymentTypes().forEach(joffer -> System.out.println(joffer.getEmploymentType()));
            request.jobEmploymentTypes().forEach(newJobOffer::addEmploymentType);
        }
        if (request.jobRequirements() != null && !request.jobRequirements().isEmpty())
            request.jobRequirements().forEach(newJobOffer::addRequirement);

        NewJobOfferResponse response = NewJobOfferResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new job offer with position name '" + request.positionName() + "'")
                .build();

        jobOfferRepository.save(newJobOffer);

        log.info("Successfully added new job offer with position name '{}'", request.positionName());

        return response;
    }
}
