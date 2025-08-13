package com.p4zd4n.kebab.services.workschedule;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.p4zd4n.kebab.entities.Employee;
import com.p4zd4n.kebab.entities.WorkScheduleEntry;
import com.p4zd4n.kebab.exceptions.alreadyexists.WorkScheduleEntryAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidTimeRangeException;
import com.p4zd4n.kebab.exceptions.notfound.EmployeeNotFoundException;
import com.p4zd4n.kebab.exceptions.notfound.WorkScheduleEntryNotFoundException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidDateOrderException;
import com.p4zd4n.kebab.exceptions.others.NullEndDateException;
import com.p4zd4n.kebab.exceptions.others.NullStartDateException;
import com.p4zd4n.kebab.exceptions.overlap.WorkScheduleTimeOverlapException;
import com.p4zd4n.kebab.repositories.EmployeesRepository;
import com.p4zd4n.kebab.repositories.WorkScheduleEntryRepository;
import com.p4zd4n.kebab.requests.workschedule.NewWorkScheduleEntryRequest;
import com.p4zd4n.kebab.responses.workschedule.NewWorkScheduleEntryResponse;
import com.p4zd4n.kebab.responses.workschedule.RemovedWorkScheduleEntryResponse;
import com.p4zd4n.kebab.responses.workschedule.WorkScheduleEntryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkScheduleService {

    private final WorkScheduleEntryRepository workScheduleEntryRepository;
    private final EmployeesRepository employeeRepository;

    public WorkScheduleService(
            WorkScheduleEntryRepository workScheduleEntryRepository,
            EmployeesRepository employeeRepository
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
                .employeeFirstName(workScheduleEntry.getEmployeeFirstName())
                .employeeLastName(workScheduleEntry.getEmployeeLastName())
                .employeeEmail(workScheduleEntry.getEmployeeEmail())
                .date(workScheduleEntry.getDate())
                .startTime(workScheduleEntry.getStartTime())
                .endTime(workScheduleEntry.getEndTime())
                .build();
    }

    public NewWorkScheduleEntryResponse addWorkScheduleEntry(NewWorkScheduleEntryRequest request) {

        Employee employee = employeeRepository.findByEmail(request.employeeEmail())
                .orElseThrow(() -> new EmployeeNotFoundException(request.employeeEmail()));

        List<WorkScheduleEntry> existingEntries = workScheduleEntryRepository.findByEmployeeEmailAndDate(
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
                .employeeFirstName(employee.getFirstName())
                .employeeLastName(employee.getLastName())
                .employeeEmail(employee.getEmail())
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

    public byte[] generateWorkSchedulePDF(LocalDate startDate, LocalDate endDate, String language) throws DocumentException {

        if (startDate == null) throw new NullStartDateException();
        if (endDate == null) throw new NullEndDateException();
        if (startDate.isAfter(endDate)) throw new InvalidDateOrderException();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);

        document.open();

        addDocumentImage(document);
        addDocumentHeader(document, startDate, endDate, language);
        addDocumentTable(document, startDate, endDate);

        document.close();

        return out.toByteArray();
    }

    private void addDocumentImage(Document document) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/static/images/logo2.png");

            byte[] imageBytes = inputStream.readAllBytes();
            Image logo = Image.getInstance(imageBytes);

            logo.scaleToFit(100, 50);

            document.add(logo);
            document.add(Chunk.NEWLINE);

        } catch (Exception e) {
            log.warn("Can't load logo image to work schedule PDF", e);
        }
    }

    private void addDocumentHeader(
            Document document, LocalDate startDate, LocalDate endDate, String language
    ) throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

        if (language.equalsIgnoreCase("pl")) {
            document.add(new Paragraph("Harmonogram pracy", headerFont));
            document.add(new Paragraph("Od: " + startDate + " Do: " + endDate, headerFont));
            document.add(Chunk.NEWLINE);
            return;
        }

        document.add(new Paragraph("Work schedule", headerFont));
        document.add(new Paragraph("From: " + startDate + " To: " + endDate, headerFont));
        document.add(Chunk.NEWLINE);
    }

    private void addDocumentTable(
            Document document, LocalDate startDate, LocalDate endDate
    ) throws DocumentException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedEmail = authentication.getName();

        Font tableFont = new Font(Font.FontFamily.HELVETICA, 8);
        List<WorkScheduleEntry> filteredWorkScheduleEntries = workScheduleEntryRepository.findAll().stream()
                .filter(entry -> !entry.getDate().isBefore(startDate) && !entry.getDate().isAfter(endDate))
                .toList();
        List<LocalDate> daysInMonth = startDate.datesUntil(endDate.plusDays(1)).toList();
        Map<String, Employee> employeesByEmail = employeeRepository.findAll().stream()
                .collect(Collectors.toMap(Employee::getEmail, e -> e));
        Map<Employee, List<WorkScheduleEntry>> entriesByEmployee =
                filteredWorkScheduleEntries.stream()
                        .filter(e -> employeesByEmail.containsKey(e.getEmployeeEmail()))
                        .collect(Collectors.groupingBy(
                                e -> employeesByEmail.get(e.getEmployeeEmail())
                        ));
        PdfPTable table = new PdfPTable(daysInMonth.size() + 1);

        table.setWidthPercentage(100);
        table.addCell(createCenteredCell("", tableFont, BaseColor.LIGHT_GRAY));

        daysInMonth.forEach(date -> table.addCell(createCenteredCell(date.format(DateTimeFormatter.ofPattern("dd.MM")), tableFont, BaseColor.LIGHT_GRAY)));
        entriesByEmployee.forEach((employee, workSchedule) -> {
            boolean isCurrentUser = employee.getEmail().equals(authenticatedEmail);

            table.addCell(createCenteredCell(
                employee.getFirstName() + " " + employee.getLastName(),
                tableFont,
                isCurrentUser ? BaseColor.RED : BaseColor.WHITE
            ));

            daysInMonth.forEach(date -> {
                List<WorkScheduleEntry> entriesForDay = workSchedule.stream()
                        .filter(entry -> entry.getDate().equals(date))
                        .toList();

                if (entriesForDay.isEmpty()) {
                    table.addCell(createCenteredCell("", tableFont, isCurrentUser ? BaseColor.RED : BaseColor.WHITE));
                } else {
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < entriesForDay.size(); i++) {
                        WorkScheduleEntry e = entriesForDay.get(i);
                        sb.append(e.getStartTime()).append(" - ").append(e.getEndTime());
                        if (i < entriesForDay.size() - 1) sb.append("\n\n");
                    }

                    table.addCell(createCenteredCell(
                        sb.toString().trim(),
                        tableFont,
                        isCurrentUser ? BaseColor.RED : BaseColor.WHITE
                    ));
                }
            });
        });

        document.add(table);
    }

    private PdfPCell createCenteredCell(String text, Font font, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(color);
        return cell;
    }

}
