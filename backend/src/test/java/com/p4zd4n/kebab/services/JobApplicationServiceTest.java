package com.p4zd4n.kebab.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.p4zd4n.kebab.entities.Cv;
import com.p4zd4n.kebab.entities.JobApplication;
import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.notfound.CvNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.JobApplicationNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.JobOfferNotFoundException;
import com.p4zd4n.kebab.repositories.CvRepository;
import com.p4zd4n.kebab.repositories.JobApplicationRepository;
import com.p4zd4n.kebab.repositories.JobOfferRepository;
import com.p4zd4n.kebab.requests.jobs.JobOfferApplicationRequest;
import com.p4zd4n.kebab.responses.jobs.JobOfferApplicationResponse;
import com.p4zd4n.kebab.responses.jobs.NewCvResponse;
import com.p4zd4n.kebab.responses.jobs.RemovedApplicationResponse;
import com.p4zd4n.kebab.services.jobs.JobApplicationService;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class JobApplicationServiceTest {

  @Mock private JobOfferRepository jobOfferRepository;

  @Mock private JobApplicationRepository jobApplicationRepository;

  @Mock private CvRepository cvRepository;

  @InjectMocks private JobApplicationService jobApplicationService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void addJobOfferApplication_ShouldAddApplicationToJobOffer_WhenCalled() {

    JobOffer mockJobOffer =
        JobOffer.builder()
            .positionName("Cook")
            .description("Description")
            .hourlyWage(BigDecimal.valueOf(20))
            .build();

    when(jobOfferRepository.findByPositionName("Cook")).thenReturn(Optional.of(mockJobOffer));

    JobOfferApplicationRequest request =
        JobOfferApplicationRequest.builder()
            .positionName("Cook")
            .applicantFirstName("Wiktor")
            .applicantLastName("C")
            .applicantTelephone("123456789")
            .applicantEmail("wiko700@gmail.com")
            .isStudent(true)
            .build();

    JobApplication newJobApplication =
        JobApplication.builder()
            .applicantFirstName("John")
            .applicantLastName("Doe")
            .applicantEmail("john.doe@example.com")
            .applicantTelephone("1234567890")
            .additionalMessage("Looking forward to this opportunity")
            .isStudent(true)
            .build();

    newJobApplication.setJobOffer(mockJobOffer);

    when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(newJobApplication);

    JobOfferApplicationResponse response = jobApplicationService.addJobOfferApplication(request);

    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals(
        "Successfully added new job offer application on position 'Cook'", response.message());

    verify(jobOfferRepository, times(1)).findByPositionName("Cook");
    verify(jobApplicationRepository, times(1)).save(any(JobApplication.class));
  }

  @Test
  public void
      addJobOfferApplication_ShouldThrowJobOfferNotFoundException_WhenJobOfferDoesNotExist() {

    when(jobOfferRepository.findByPositionName("Cook")).thenReturn(Optional.empty());

    JobOfferApplicationRequest request =
        JobOfferApplicationRequest.builder()
            .positionName("Cook")
            .applicantFirstName("Wiktor")
            .applicantLastName("C")
            .applicantTelephone("123456789")
            .applicantEmail("wiko700@gmail.com")
            .isStudent(true)
            .build();

    JobOfferNotFoundException exception =
        assertThrows(
            JobOfferNotFoundException.class,
            () -> jobApplicationService.addJobOfferApplication(request));

    assertEquals("Job offer with position name 'Cook' not found!", exception.getMessage());

    verify(jobOfferRepository, times(1)).findByPositionName("Cook");
  }

  @Test
  public void addCv_ShouldAddCv_WhenCalled() throws IOException {

    Long applicationId = 1L;
    String cvFileName = "cv.pdf";
    byte[] cvFileData = "cv".getBytes();

    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getOriginalFilename()).thenReturn("cv.pdf");
    when(multipartFile.getBytes()).thenReturn(cvFileData);

    JobApplication jobApplication =
        JobApplication.builder()
            .applicantFirstName("John")
            .applicantLastName("Doe")
            .applicantEmail("john.doe@example.com")
            .applicantTelephone("1234567890")
            .additionalMessage("Looking forward to this opportunity")
            .isStudent(true)
            .build();
    jobApplication.setId(applicationId);

    when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(jobApplication));

    Cv newCv = Cv.builder().fileName(cvFileName).data(cvFileData).build();

    when(cvRepository.save(any(Cv.class))).thenReturn(newCv);

    NewCvResponse response = jobApplicationService.addCv(multipartFile, applicationId);

    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals(
        "Successfully added cv with name 'cv.pdf' to job application with id '1'!",
        response.message());

    verify(jobApplicationRepository, times(1)).findById(applicationId);
    verify(cvRepository, times(1)).save(any(Cv.class));
    verify(jobApplicationRepository, times(1)).save(jobApplication);
  }

  @Test
  public void addCv_ShouldThrowJobApplicationFoundException_WhenJobApplicationDoesNotExist() {

    Long applicationId = 99L;
    MultipartFile multipartFile = mock(MultipartFile.class);

    when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

    JobApplicationNotFoundException exception =
        assertThrows(
            JobApplicationNotFoundException.class,
            () -> jobApplicationService.addCv(multipartFile, applicationId));

    assertEquals("Job application with id '99' not found!", exception.getMessage());

    verify(jobApplicationRepository, times(1)).findById(applicationId);
  }

  @Test
  public void getCv_ShouldRetrieveCv_WhenCalled() {

    Long cvId = 1L;
    String cvFileName = "cv.pdf";
    byte[] cvFileData = "cv".getBytes();

    Cv newCv = Cv.builder().fileName(cvFileName).data(cvFileData).build();

    when(cvRepository.findById(cvId)).thenReturn(Optional.of(newCv));

    ResponseEntity<byte[]> response = jobApplicationService.getCv(cvId);

    assertNotNull(response);
    assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    assertEquals(
        "attachment; filename=\"" + cvFileName + "\"",
        response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
    assertArrayEquals(cvFileData, response.getBody());
  }

  @Test
  public void getCv_ShouldThrowCvNotFoundException_WhenCvNotFound() {

    Long cvId = 56L;

    when(cvRepository.findById(cvId)).thenReturn(Optional.empty());

    CvNotFoundException exception =
        assertThrows(CvNotFoundException.class, () -> jobApplicationService.getCv(cvId));

    assertEquals("CV with id '56' not found!", exception.getMessage());

    verify(cvRepository, times(1)).findById(cvId);
  }

  @Test
  public void removeJobApplication_ShouldRemoveJobApplication_WhenCalled() {

    Long applicationId = 1L;

    JobApplication jobApplication =
        JobApplication.builder()
            .applicantFirstName("John")
            .applicantLastName("Doe")
            .applicantEmail("john.doe@example.com")
            .applicantTelephone("1234567890")
            .additionalMessage("Looking forward to this opportunity")
            .isStudent(true)
            .build();
    jobApplication.setId(applicationId);

    JobOffer mockJobOffer =
        JobOffer.builder()
            .positionName("Cook")
            .description("Description")
            .hourlyWage(BigDecimal.valueOf(20))
            .build();
    mockJobOffer.getJobApplications().add(jobApplication);

    when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(jobApplication));

    RemovedApplicationResponse response =
        jobApplicationService.removeJobApplication(applicationId, mockJobOffer);

    assertEquals(HttpStatus.OK.value(), response.statusCode());
    assertEquals(
        "Successfully removed job application with id '1' from job offer with position name 'Cook'",
        response.message());
    assertFalse(mockJobOffer.getJobApplications().contains(jobApplication));

    verify(jobApplicationRepository, times(1)).delete(jobApplication);
    verify(jobOfferRepository, times(1)).save(mockJobOffer);
  }

  @Test
  public void
      removeJobApplication_ShouldThrowJobApplicationNotFoundException_WhenApplicationDoesNotExist() {

    Long applicationId = 56L;
    JobOffer mockJobOffer =
        JobOffer.builder()
            .positionName("Cook")
            .description("Description")
            .hourlyWage(BigDecimal.valueOf(20))
            .build();

    when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

    JobApplicationNotFoundException exception =
        assertThrows(
            JobApplicationNotFoundException.class,
            () -> jobApplicationService.removeJobApplication(applicationId, mockJobOffer));

    assertEquals("Job application with id '56' not found!", exception.getMessage());

    verify(jobApplicationRepository, times(1)).findById(applicationId);
  }
}
