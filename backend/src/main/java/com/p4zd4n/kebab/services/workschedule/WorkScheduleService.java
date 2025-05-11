package com.p4zd4n.kebab.services.workschedule;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.WorkScheduleEntry;
import com.p4zd4n.kebab.exceptions.alreadyexists.WorkScheduleEntryAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidTimeRangeException;
import com.p4zd4n.kebab.exceptions.notfound.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.WorkScheduleEntryNotFoundException;
import com.p4zd4n.kebab.exceptions.overlap.WorkScheduleTimeOverlapException;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.repositories.WorkScheduleEntryRepository;
import com.p4zd4n.kebab.requests.workschedule.NewWorkScheduleEntryRequest;
import com.p4zd4n.kebab.responses.menu.addons.RemovedAddonResponse;
import com.p4zd4n.kebab.responses.workschedule.NewWorkScheduleEntryResponse;
import com.p4zd4n.kebab.responses.workschedule.RemovedWorkScheduleEntryResponse;
import com.p4zd4n.kebab.responses.workschedule.WorkScheduleEntryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
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
                .id(workScheduleEntry.getId())
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

    public WorkScheduleEntry findWorkScheduleEntryById(Long id) {

        log.info("Started finding work schedule entry with id '{}'", id);

        WorkScheduleEntry workScheduleEntry = workScheduleEntryRepository.findById(id)
                .orElseThrow(() -> new WorkScheduleEntryNotFoundException(id));

        log.info("Successfully found work schedule entry with id '{}'", id);

        return workScheduleEntry;
    }

    public RemovedWorkScheduleEntryResponse removeWorkScheduleEntry(WorkScheduleEntry workScheduleEntry) {
        log.info("Started removing work schedule entry with id '{}'", workScheduleEntry.getId());

        workScheduleEntryRepository.delete(workScheduleEntry);

        RemovedWorkScheduleEntryResponse response = RemovedWorkScheduleEntryResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed work schedule entry with id '" + workScheduleEntry.getId() + "'")
                .build();

        log.info("Successfully removed work schedule entry with id '{}'", workScheduleEntry.getId());

        return response;
    }
}
