package com.p4zd4n.kebab.services.jobs;

import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.alreadyexists.JobOfferAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.JobOfferNotFoundException;
import com.p4zd4n.kebab.repositories.JobOfferRepository;
import com.p4zd4n.kebab.requests.jobs.NewJobOfferRequest;
import com.p4zd4n.kebab.requests.jobs.UpdatedJobOfferRequest;
import com.p4zd4n.kebab.responses.jobs.JobOfferGeneralResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferManagerResponse;
import com.p4zd4n.kebab.responses.jobs.NewJobOfferResponse;
import com.p4zd4n.kebab.responses.jobs.UpdatedJobOfferResponse;
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

        if (request.jobEmploymentTypes() != null && !request.jobEmploymentTypes().isEmpty())
            request.jobEmploymentTypes().forEach(newJobOffer::addEmploymentType);
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

    public JobOffer findJobOfferByPositionName(String positionName) {

        log.info("Started finding job offer with position name '{}'", positionName);

        JobOffer jobOffer = jobOfferRepository.findByPositionName(positionName)
                .orElseThrow(() -> new JobOfferNotFoundException(positionName));

        log.info("Successfully found job offer with position name '{}'", positionName);

        return jobOffer;
    }

    public UpdatedJobOfferResponse updateJobOffer(JobOffer jobOffer, UpdatedJobOfferRequest request) {

        Optional<JobOffer> optionalJobOffer = jobOfferRepository.findByPositionName(request.updatedPositionName());

        if (optionalJobOffer.isPresent()) {

            boolean exists = optionalJobOffer.get().getPositionName().equalsIgnoreCase(request.updatedPositionName());

            if (exists) throw new JobOfferAlreadyExistsException(request.updatedPositionName());
        }

        UpdatedJobOfferResponse response = UpdatedJobOfferResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated job offer with position name '" + jobOffer.getPositionName() + "'")
                .build();

        jobOffer.setPositionName(request.updatedPositionName());
        jobOffer.setDescription(request.updatedDescription());
        jobOffer.setHourlyWage(request.updatedHourlyWage());

        jobOffer.getJobEmploymentTypes().clear();
        if (request.updatedEmploymentTypes() != null && !request.updatedEmploymentTypes().isEmpty()) {
            request.updatedEmploymentTypes().forEach(jobOffer::addEmploymentType);
        }

        jobOffer.getJobRequirements().clear();
        if (request.updatedRequirements() != null && !request.updatedRequirements().isEmpty())
            request.updatedRequirements().forEach(jobOffer::addRequirement);

        jobOfferRepository.save(jobOffer);

        return response;
    }
}
