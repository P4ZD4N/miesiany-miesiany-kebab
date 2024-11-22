package com.p4zd4n.kebab.services.jobs;

import com.p4zd4n.kebab.entities.Cv;
import com.p4zd4n.kebab.entities.JobApplication;
import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.notfound.CvNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.JobApplicationNotFound;
import com.p4zd4n.kebab.exceptions.notfound.JobOfferNotFoundException;
import com.p4zd4n.kebab.repositories.CvRepository;
import com.p4zd4n.kebab.repositories.JobApplicationRepository;
import com.p4zd4n.kebab.repositories.JobOfferRepository;
import com.p4zd4n.kebab.requests.jobs.JobOfferApplicationRequest;
import com.p4zd4n.kebab.responses.jobs.NewCvResponse;
import com.p4zd4n.kebab.responses.jobs.JobOfferApplicationResponse;
import com.p4zd4n.kebab.responses.jobs.RemovedApplicationResponse;
import com.p4zd4n.kebab.responses.jobs.RemovedJobOfferResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class JobApplicationService {

    private final JobOfferRepository jobOfferRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CvRepository cvRepository;

    public JobApplicationService(
            JobOfferRepository jobOfferRepository,
            JobApplicationRepository jobApplicationRepository,
            CvRepository cvRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.cvRepository = cvRepository;
        this.jobOfferRepository = jobOfferRepository;
    }

    public JobOfferApplicationResponse addJobOfferApplication(JobOfferApplicationRequest request) {

        log.info("Started adding job offer application on position '{}'", request.positionName());

        JobApplication newJobApplication = JobApplication.builder()
                .applicantFirstName(request.applicantFirstName())
                .applicantLastName(request.applicantLastName())
                .applicantEmail(request.applicantEmail())
                .applicantTelephone(request.applicantTelephone())
                .additionalMessage(request.additionalMessage())
                .isStudent(request.isStudent())
                .build();
        newJobApplication.setJobOffer(jobOfferRepository.findByPositionName(request.positionName()).orElseThrow(
                () -> new JobOfferNotFoundException(request.positionName())
        ));

        JobApplication savedNewJobApplication = jobApplicationRepository.save(newJobApplication);

        JobOfferApplicationResponse response = JobOfferApplicationResponse.builder()
                .applicationId(savedNewJobApplication.getId())
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new job offer application on position '" + request.positionName() + "'")
                .build();

        log.info("Successfully added new job offer application on position '{}'", request.positionName());

        return response;
    }

    public NewCvResponse addCv(MultipartFile cv, Long applicationId) throws IOException {

        log.info("Started adding cv with name '{}'", cv.getOriginalFilename());

        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new JobApplicationNotFound(applicationId));

        Cv newCv = Cv.builder()
                .fileName(cv.getOriginalFilename())
                .data(cv.getBytes())
                .build();

        newCv.setJobApplication(jobApplication);
        jobApplication.setCv(newCv);

        NewCvResponse response = NewCvResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added cv with name '" + cv.getOriginalFilename() + "' to job application with id '" + applicationId + "'!")
                .build();

        cvRepository.save(newCv);
        jobApplicationRepository.save(jobApplication);

        log.info("Successfully added cv with name: '{}' to job application with id: '{}'", cv.getOriginalFilename(), applicationId);

        return response;
    }

    public ResponseEntity<byte[]> getCv(Long id) {

        log.info("Started retrieving CV with id '{}'", id);

        Cv cv = cvRepository.findById(id)
                .orElseThrow(() -> new CvNotFoundException(id));

        log.info("Successfully retrieved CV with id '{}'", id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cv.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(cv.getData());
    }

    public RemovedApplicationResponse removeJobApplication(Long applicationId, JobOffer jobOffer) {
        log.info(
                "Started removing job application with id '{}' from job offer with position name {}",
                applicationId,
                jobOffer.getPositionName()
        );

        JobApplication jobApplication = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new JobApplicationNotFound(applicationId));
        jobOffer.getJobApplications().remove(jobApplication);

        jobApplicationRepository.delete(jobApplication);
        jobOfferRepository.save(jobOffer);

        RemovedApplicationResponse response = RemovedApplicationResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(
                        "Successfully removed job application with id '" +
                        applicationId +
                        "' from job offer with position name '" +
                        jobOffer.getPositionName() + "'"
                )
                .build();

        log.info(
                "Successfully removed job application with id '{}' from job offer with position name {}",
                applicationId,
                jobOffer.getPositionName()
        );

        return response;
    }
}
