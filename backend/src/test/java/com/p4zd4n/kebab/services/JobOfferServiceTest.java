package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.entities.JobOffer;
import com.p4zd4n.kebab.exceptions.alreadyexists.JobOfferAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.notfound.JobOfferNotFoundException;
import com.p4zd4n.kebab.repositories.JobOfferRepository;
import com.p4zd4n.kebab.requests.jobs.NewJobOfferRequest;
import com.p4zd4n.kebab.requests.jobs.RemovedJobOfferRequest;
import com.p4zd4n.kebab.requests.jobs.UpdatedJobOfferRequest;
import com.p4zd4n.kebab.responses.jobs.*;
import com.p4zd4n.kebab.responses.menu.addons.AddonResponse;
import com.p4zd4n.kebab.services.jobs.JobOfferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobOfferServiceTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @InjectMocks
    private JobOfferService jobOfferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getJobOffersForOtherUsers_ShouldReturnJobOffers_WhenCalled() {

        List<JobOffer> jobOffers = Arrays.asList(
                JobOffer.builder()
                        .positionName("Cook")
                        .description("Description")
                        .hourlyWage(BigDecimal.valueOf(20))
                        .build(),
                JobOffer.builder()
                        .positionName("Cashier")
                        .description("Description")
                        .hourlyWage(BigDecimal.valueOf(23))
                        .build()
        );

        when(jobOfferRepository.findAll()).thenReturn(jobOffers);

        List<JobOfferGeneralResponse> result = jobOfferService.getJobOffersForOtherUsers();

        assertEquals(2, result.size());

        assertEquals("Cook", result.getFirst().positionName());
        assertEquals(BigDecimal.valueOf(23), result.getLast().hourlyWage());

        verify(jobOfferRepository, times(1)).findAll();
    }

    @Test
    public void getJobOffersForManager_ShouldReturnJobOffers_WhenCalled() {

        List<JobOffer> jobOffers = Arrays.asList(
                JobOffer.builder()
                        .positionName("Cook")
                        .description("Description")
                        .hourlyWage(BigDecimal.valueOf(20))
                        .build(),
                JobOffer.builder()
                        .positionName("Cashier")
                        .description("Description")
                        .hourlyWage(BigDecimal.valueOf(23))
                        .build()
        );

        when(jobOfferRepository.findAll()).thenReturn(jobOffers);

        List<JobOfferManagerResponse> result = jobOfferService.getJobOffersForManager();

        assertEquals(2, result.size());

        assertEquals("Cook", result.getFirst().positionName());
        assertEquals(BigDecimal.valueOf(23), result.getLast().hourlyWage());

        verify(jobOfferRepository, times(1)).findAll();
    }

    @Test
    public void addJobOffer_ShouldAddJobOffer_WhenJobWithSamePositionDoesNotExist() {

        NewJobOfferRequest request = NewJobOfferRequest.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(24))
                .build();

        when(jobOfferRepository.findByPositionName(request.positionName())).thenReturn(Optional.empty());

        JobOffer newJobOffer = JobOffer.builder()
                .positionName(request.positionName())
                .description(request.description())
                .hourlyWage(request.hourlyWage())
                .build();

        when(jobOfferRepository.save(any(JobOffer.class))).thenReturn(newJobOffer);

        NewJobOfferResponse response = jobOfferService.addJobOffer(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully added new job offer with position name 'Cook'", response.message());

        verify(jobOfferRepository, times(1)).findByPositionName(request.positionName());
        verify(jobOfferRepository, times(1)).save(any(JobOffer.class));
    }

    @Test
    public void addJobOffer_ShouldThrowJobOfferAlreadyExistsException_WhenJobOfferWithSamePositionDoesNotExist() {

        NewJobOfferRequest request = NewJobOfferRequest.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(24))
                .build();

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("New description")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();

        when(jobOfferRepository.findByPositionName(request.positionName())).thenReturn(Optional.of(existingJobOffer));

        assertThrows(JobOfferAlreadyExistsException.class, () -> {
            jobOfferService.addJobOffer(request);
        });

        verify(jobOfferRepository, times(1)).findByPositionName(request.positionName());
    }

    @Test
    public void findJobOfferByPositionName_ShouldReturnJobOffer_WhenJobOfferExists() {

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();

        when(jobOfferRepository.findByPositionName("Cook")).thenReturn(Optional.of(existingJobOffer));

        JobOffer foundJobOffer = jobOfferService.findJobOfferByPositionName("Cook");

        assertNotNull(foundJobOffer);
        assertEquals("Cook", foundJobOffer.getPositionName());
        assertEquals(BigDecimal.valueOf(25), foundJobOffer.getHourlyWage());

        verify(jobOfferRepository, times(1)).findByPositionName("Cook");
    }

    @Test
    public void findJobOfferByPositionName_ShouldThrowJobOfferNotFoundException_WhenJobOfferDoesNotExist() {

        when(jobOfferRepository.findByPositionName("Cook")).thenThrow(new JobOfferNotFoundException("Cook"));

        JobOfferNotFoundException exception = assertThrows(JobOfferNotFoundException.class, () -> {
            jobOfferService.findJobOfferByPositionName("Cook");
        });

        assertEquals("Job offer with position name 'Cook' not found!", exception.getMessage());

        verify(jobOfferRepository, times(1)).findByPositionName("Cook");
    }

    @Test
    public void updateJobOffer_ShouldUpdateJobOffer_WhenCalled() {

        JobOffer jobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();

        UpdatedJobOfferRequest request = UpdatedJobOfferRequest.builder()
                .positionName("Cook")
                .updatedPositionName("Coook")
                .updatedDescription("Updated description")
                .updatedHourlyWage(BigDecimal.valueOf(24))
                .build();

        when(jobOfferRepository.findByPositionName(jobOffer.getPositionName())).thenReturn(Optional.of(jobOffer));
        when(jobOfferRepository.save(any(JobOffer.class))).thenReturn(jobOffer);

        UpdatedJobOfferResponse response = jobOfferService.updateJobOffer(jobOffer, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully updated job offer with position name 'Cook'", response.message());
        assertEquals("Coook", jobOffer.getPositionName());

        verify(jobOfferRepository, times(1)).findByPositionName(jobOffer.getPositionName());
        verify(jobOfferRepository, times(1)).save(jobOffer);
    }

    @Test
    public void updateJobOffer_ShouldThrowJobOfferAlreadyExistsException_WhenJobOfferExists() {

        JobOffer existingJobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();
        existingJobOffer.setId(1L);

        JobOffer jobOfferToUpdate = JobOffer.builder()
                .positionName("Cashier")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();
        jobOfferToUpdate.setId(2L);

        UpdatedJobOfferRequest request = UpdatedJobOfferRequest.builder()
                .positionName(jobOfferToUpdate.getPositionName())
                .updatedPositionName("Cook")
                .updatedDescription("Updated description")
                .updatedHourlyWage(BigDecimal.valueOf(20))
                .build();

        when(jobOfferRepository.findByPositionName(request.updatedPositionName()))
                .thenReturn(Optional.of(existingJobOffer));

        assertThrows(JobOfferAlreadyExistsException.class, () -> {
            jobOfferService.updateJobOffer(jobOfferToUpdate, request);
        });

        verify(jobOfferRepository, times(1)).findByPositionName(request.updatedPositionName());
    }

    @Test
    public void removeJobOffer_ShouldRemoveJobOffer_WhenCalled() {

        JobOffer jobOffer = JobOffer.builder()
                .positionName("Cook")
                .description("Description")
                .hourlyWage(BigDecimal.valueOf(25))
                .build();

        doNothing().when(jobOfferRepository).delete(jobOffer);

        RemovedJobOfferResponse response = jobOfferService.removeJobOffer(jobOffer);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully removed job offer with position name 'Cook'", response.message());

        verify(jobOfferRepository, times(1)).delete(jobOffer);
    }
}
