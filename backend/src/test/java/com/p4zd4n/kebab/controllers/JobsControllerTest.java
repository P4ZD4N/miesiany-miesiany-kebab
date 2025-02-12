package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.GlobalExceptionHandler;
import com.p4zd4n.kebab.exceptions.alreadyexists.JobOfferAlreadyExistsException;
import com.p4zd4n.kebab.requests.jobs.*;
import com.p4zd4n.kebab.responses.jobs.*;
import com.p4zd4n.kebab.services.jobs.JobApplicationService;
import com.p4zd4n.kebab.services.jobs.JobOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class JobsControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobOfferService jobOfferService;

    @MockBean
    private JobApplicationService jobApplicationService;

    @InjectMocks
    private JobsController jobsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getJobOffersForManager_ShouldReturnJobOffers_WhenCalled() throws Exception {

        List<JobOfferManagerResponse> jobOffers = Arrays.asList(
            JobOfferManagerResponse.builder()
                    .positionName("Cook")
                    .build(),
                JobOfferManagerResponse.builder()
                        .positionName("Cashier")
                        .build()
        );

        when(jobOfferService.getJobOffersForManager()).thenReturn(jobOffers);

        mockMvc.perform(get("/api/v1/jobs/job-offers/manager"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].position_name", is("Cook")))
                .andExpect(jsonPath("$[1].position_name", is("Cashier")));

        verify(jobOfferService, times(1)).getJobOffersForManager();
    }

    @Test
    public void getJobOffersForOtherUsers_ShouldReturnJobOffers_WhenCalled() throws Exception {

        List<JobOfferGeneralResponse> jobOffers = Arrays.asList(
                JobOfferGeneralResponse.builder()
                        .positionName("Cook")
                        .build(),
                JobOfferGeneralResponse.builder()
                        .positionName("Cashier")
                        .description("Description")
                        .build()
        );

        when(jobOfferService.getJobOffersForOtherUsers()).thenReturn(jobOffers);

        mockMvc.perform(get("/api/v1/jobs/job-offers/general"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].position_name", is("Cook")))
                .andExpect(jsonPath("$[1].description", is("Description")));

        verify(jobOfferService, times(1)).getJobOffersForOtherUsers();
    }

    @Test
    public void addJobOffer_ShouldReturnOk_WhenValidRequest() throws Exception {

        NewJobOfferRequest request = NewJobOfferRequest.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        NewJobOfferResponse response = NewJobOfferResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new job offer with position name 'Cook'")
                .build();

        when(jobOfferService.addJobOffer(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/add-job-offer")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new job offer with position name 'Cook'")));
    }

    @Test
    public void addJobOffer_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {

        NewJobOfferRequest request = NewJobOfferRequest.builder()
                .positionName("Cook")
                .build();

        mockMvc.perform(post("/api/v1/jobs/add-job-offer")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addJobOffer_ShouldReturnConflict_WhenJobOfferExists() throws Exception {

        NewJobOfferRequest request = NewJobOfferRequest.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        when(jobOfferService.addJobOffer(request)).thenThrow(new JobOfferAlreadyExistsException(request.positionName()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("jobOffer.alreadyExists", null, Locale.forLanguageTag("en")))
                .thenReturn("Job offer with the same name already exists!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new JobsController(jobOfferService, jobApplicationService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(post("/api/v1/jobs/add-job-offer")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Job offer with the same name already exists!")));
    }

    @Test
    public void addJobOffer_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        NewJobOfferRequest request = NewJobOfferRequest.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        mockMvc.perform(post("/api/v1/jobs/add-job-offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addJobOffer_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(post("/api/v1/jobs/add-job-offer")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void updateJobOffer_ShouldReturnOk_WhenValidRequest() throws Exception {

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        UpdatedJobOfferRequest request = UpdatedJobOfferRequest.builder()
                .positionName("Cook")
                .updatedPositionName("Coook")
                .updatedDescription("Description")
                .updatedHourlyWage(BigDecimal.valueOf(20))
                .build();

        UpdatedJobOfferResponse response = UpdatedJobOfferResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated job offer with position name 'Cook'")
                .build();

        when(jobOfferService.findJobOfferByPositionName(request.positionName())).thenReturn(existingJobOffer);
        when(jobOfferService.updateJobOffer(existingJobOffer, request)).thenReturn(response);

        mockMvc.perform(put("/api/v1/jobs/update-job-offer")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated job offer with position name 'Cook'")));

        verify(jobOfferService, times(1)).findJobOfferByPositionName(request.positionName());
        verify(jobOfferService, times(1)).updateJobOffer(existingJobOffer, request);
    }

    @Test
    public void updateJobOffer_ShouldReturnConflict_WheJobOfferWithSameNameAlreadyExists() throws Exception {

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        UpdatedJobOfferRequest request = UpdatedJobOfferRequest.builder()
                .positionName("Cook")
                .updatedPositionName("Cashier")
                .updatedDescription("Description")
                .updatedHourlyWage(BigDecimal.valueOf(20))
                .build();

        when(jobOfferService.findJobOfferByPositionName(request.positionName())).thenReturn(existingJobOffer);
        when(jobOfferService.updateJobOffer(existingJobOffer, request)).thenThrow(new JobOfferAlreadyExistsException(request.updatedPositionName()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("jobOffer.alreadyExists", null, Locale.forLanguageTag("pl")))
                .thenReturn("Oferta pracy na ta pozycje juz istnieje!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new JobsController(jobOfferService, jobApplicationService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/jobs/update-job-offer")
                .header("Accept-Language", "pl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status_code", is(409)))
                .andExpect(jsonPath("$.message", is("Oferta pracy na ta pozycje juz istnieje!")));

        verify(jobOfferService, times(1)).updateJobOffer(existingJobOffer, request);
    }

    @Test
    public void updateJobOffer_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        UpdatedJobOfferRequest request = UpdatedJobOfferRequest.builder()
                .positionName("Cook")
                .updatedDescription("Description")
                .updatedHourlyWage(BigDecimal.valueOf(20))
                .build();

        mockMvc.perform(put("/api/v1/jobs/update-job-offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateJobOffer_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/jobs/update-job-offer")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    public void removeJobOffer_ShouldReturnOk_WhenValidRequest() throws Exception {

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        RemovedJobOfferRequest request = RemovedJobOfferRequest.builder()
                .positionName("Cook")
                .build();

        RemovedJobOfferResponse response = RemovedJobOfferResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed beverage with position name 'Cook'")
                .build();

        when(jobOfferService.findJobOfferByPositionName(existingJobOffer.getPositionName())).thenReturn(existingJobOffer);
        when(jobOfferService.removeJobOffer(any(JobOffer.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/jobs/remove-job-offer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed beverage with position name 'Cook'")));

        verify(jobOfferService, times(1)).findJobOfferByPositionName(request.positionName());
        verify(jobOfferService, times(1)).removeJobOffer(existingJobOffer);
    }

    @Test
    public void addJobApplication_ShouldReturnOk_WhenValidRequest() throws Exception {

        JobOfferApplicationRequest request = JobOfferApplicationRequest.builder()
                .positionName("Cook")
                .applicantFirstName("Wiktor")
                .applicantLastName("Chudy")
                .applicantTelephone("123456789")
                .applicantEmail("wiko700@gmail.com")
                .isStudent(true)
                .build();

        JobOfferApplicationResponse response = JobOfferApplicationResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new job offer application on position 'Cook'")
                .build();

        when(jobApplicationService.addJobOfferApplication(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/jobs/add-job-offer-application")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added new job offer application on position 'Cook'")));
    }

    @Test
    public void removeApplication_ShouldReturnOk_WhenValidRequest() throws Exception {

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(20))
                .build();

        RemovedApplicationRequest request = RemovedApplicationRequest.builder()
                .positionName("Cook")
                .applicationId(1L)
                .build();

        RemovedApplicationResponse response = RemovedApplicationResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed job application with id 1 from job offer with position name Cook")
                .build();

        when(jobOfferService.findJobOfferByPositionName(request.positionName())).thenReturn(existingJobOffer);
        when(jobApplicationService.removeJobApplication(request.applicationId(), existingJobOffer)).thenReturn(response);

        mockMvc.perform(delete("/api/v1/jobs/remove-job-application")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully removed job application with id 1 from job offer with position name Cook")));

        verify(jobOfferService, times(1)).findJobOfferByPositionName(request.positionName());
        verify(jobApplicationService, times(1)).removeJobApplication(request.applicationId(), existingJobOffer);
    }

    @Test
    public void addCv_ShouldReturnOk_WhenCvIsUploaded() throws Exception {
        Long applicationId = 1L;
        MockMultipartFile cvFile = new MockMultipartFile(
                "cv",
                "cv.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test CV content".getBytes()
        );

        NewCvResponse response = NewCvResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added cv with name 'cv.pdf' to job application with id '1'!")
                .build();

        when(jobApplicationService.addCv(any(MultipartFile.class), eq(applicationId))).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/jobs/add-cv")
                .file(cvFile)
                .param("applicationId", String.valueOf(applicationId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully added cv with name 'cv.pdf' to job application with id '1'!")));

        verify(jobApplicationService, times(1)).addCv(any(MultipartFile.class), eq(applicationId));
    }

    @Test
    public void downloadCv_ShouldReturnCvFile_WhenIdIsValid() throws Exception {
        Long cvId = 1L;
        byte[] fileContent = "Test CV content".getBytes();
        String fileName = "cv.pdf";

        when(jobApplicationService.getCv(cvId))
                .thenReturn(ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(fileContent));

        mockMvc.perform(get("/api/v1/jobs/download-cv/{id}", cvId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cv.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(fileContent));

        verify(jobApplicationService, times(1)).getCv(cvId);
    }
}
