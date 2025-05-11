package com.p4zd4n.kebab.services.workschedule;

import com.p4zd4n.kebab.entities.WorkScheduleEntry;
import com.p4zd4n.kebab.repositories.WorkScheduleEntryRepository;
import com.p4zd4n.kebab.responses.workschedule.WorkScheduleEntryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkScheduleService {

    private final WorkScheduleEntryRepository workScheduleEntryRepository;

    public WorkScheduleService(WorkScheduleEntryRepository workScheduleEntryRepository) {
        this.workScheduleEntryRepository = workScheduleEntryRepository;
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
}
