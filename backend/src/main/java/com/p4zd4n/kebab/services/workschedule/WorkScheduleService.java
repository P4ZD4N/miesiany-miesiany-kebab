package com.p4zd4n.kebab.services.workschedule;

import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.NewsletterSubscriber;
import com.p4zd4n.kebab.entities.WorkScheduleEntry;
import com.p4zd4n.kebab.enums.NewsletterMessagesLanguage;
import com.p4zd4n.kebab.exceptions.alreadyexists.SubscriberAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.alreadyexists.WorkScheduleEntryAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidTimeRangeException;
import com.p4zd4n.kebab.exceptions.notfound.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.overlap.WorkScheduleTimeOverlapException;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.repositories.WorkScheduleEntryRepository;
import com.p4zd4n.kebab.requests.workschedule.NewWorkScheduleEntryRequest;
import com.p4zd4n.kebab.responses.newsletter.NewNewsletterSubscriberResponse;
import com.p4zd4n.kebab.responses.workschedule.NewWorkScheduleEntryResponse;
import com.p4zd4n.kebab.responses.workschedule.WorkScheduleEntryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkScheduleService {

    private final WorkScheduleEntryRepository workScheduleEntryRepository;
    private final EmployeeRepository employeeRepository;

    public WorkScheduleService(
            WorkScheduleEntryRepository workScheduleEntryRepository,
            EmployeeRepository employeeRepository
    ) {
        this.workScheduleEntryRepository = workScheduleEntryRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<WorkScheduleEntryResponse> getWorkScheduleEntries() {
        log.info("Started retrieving work schedule entries");

        List<WorkScheduleEntry> workScheduleEntries = workScheduleEntryRepository.findAll();

        List<WorkScheduleEntryResponse> response = workScheduleEntries.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved work schedule entries");

        return response;
    }

    public WorkScheduleEntryResponse mapToResponse(WorkScheduleEntry workScheduleEntry) {

        return WorkScheduleEntryResponse.builder()
                .employee(workScheduleEntry.getEmployee())
                .date(workScheduleEntry.getDate())
                .startTime(workScheduleEntry.getStartTime())
                .endTime(workScheduleEntry.getEndTime())
                .build();
    }

    public NewWorkScheduleEntryResponse addWorkScheduleEntry(NewWorkScheduleEntryRequest request) {

        Employee employee = employeeRepository.findByEmail(request.employeeEmail())
                .orElseThrow(() -> new EmployeeNotFoundException(request.employeeEmail()));

        List<WorkScheduleEntry> existingEntries = workScheduleEntryRepository.findByEmployee_EmailAndDate(
                request.employeeEmail(),
                request.date()
        );

        LocalTime newStartTime = request.startTime();
        LocalTime newEndTime = request.endTime();

        if (newStartTime.isAfter(newEndTime) || newStartTime.equals(newEndTime)) {
            throw new InvalidTimeRangeException(newStartTime, newEndTime);
        }

        existingEntries.forEach(entry -> {
            LocalTime existingStartTime = entry.getStartTime();
            LocalTime existingEndTime = entry.getEndTime();

            if (newStartTime.isBefore(existingEndTime) && newEndTime.isAfter(existingStartTime)) {
                throw new WorkScheduleTimeOverlapException(request.employeeEmail(), request.date(), newStartTime, newEndTime);
            }

            if (newStartTime.equals(existingStartTime) && newEndTime.equals(existingEndTime)) {
                throw new WorkScheduleEntryAlreadyExistsException(request.employeeEmail(), request.date(), newStartTime, newEndTime);
            }
        });

        WorkScheduleEntry newWorkScheduleEntry = WorkScheduleEntry.builder()
                .employee(employee)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .build();

        workScheduleEntryRepository.save(newWorkScheduleEntry);

        return NewWorkScheduleEntryResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new work schedule entry for employee with email '" + request.employeeEmail() + "'")
                .build();
    }
}
