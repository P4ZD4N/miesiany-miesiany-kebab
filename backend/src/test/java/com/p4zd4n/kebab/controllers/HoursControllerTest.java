package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.enums.DayOfWeek;
import com.p4zd4n.kebab.repositories.EmployeeRepository;
import com.p4zd4n.kebab.repositories.OpeningHoursRepository;
import com.p4zd4n.kebab.requests.hour.UpdatedHourRequest;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.services.hours.HoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HoursController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HoursControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HoursService hoursService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private OpeningHoursRepository openingHoursRepository;

    @InjectMocks
    private HoursController hoursController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getOpeningHours_ShouldReturnOpeningHours_WhenCalled() throws Exception {

        List<OpeningHoursResponse> openingHoursList = Arrays.asList(
                new OpeningHoursResponse(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                new OpeningHoursResponse(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(18, 0))
        );

        when(hoursService.getOpeningHours()).thenReturn(openingHoursList);

        mockMvc.perform(get("/api/v1/hours/opening-hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].day_of_week", is("MONDAY")))
                .andExpect(jsonPath("$[0].opening_time", is("09:00:00")))
                .andExpect(jsonPath("$[0].closing_time", is("18:00:00")));

        verify(hoursService, times(1)).getOpeningHours();
    }

    @Test
    public void updateOpeningHour_ShouldUpdateOpeningHourSuccessfully_WhenValidRequest() throws Exception {

        UpdatedHourRequest request = new UpdatedHourRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(10, 0),
                LocalTime.of(20, 0)
        );

        OpeningHour existingHour = OpeningHour.builder()
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .openingTime(LocalTime.of(10, 0))
                        .closingTime(LocalTime.of(20, 0))
                        .build();

        when(hoursService.findOpeningHourByDayOfWeek(DayOfWeek.MONDAY)).thenReturn(existingHour);
        when(hoursService.saveOpeningHour(existingHour)).thenReturn(existingHour);

        mockMvc.perform(put("/api/v1/hours/update-opening-hour")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek", is("MONDAY")))
                .andExpect(jsonPath("$.openingTime", is("10:00:00")))
                .andExpect(jsonPath("$.closingTime", is("20:00:00")));

        verify(hoursService, times(1)).findOpeningHourByDayOfWeek(DayOfWeek.MONDAY);
        verify(hoursService, times(1)).saveOpeningHour(existingHour);
    }

    @Test
    public void updateOpeningHour_ShouldReturnBadRequest_WhenClosingTimeBeforeOpeningTime() throws Exception {

        UpdatedHourRequest request = new UpdatedHourRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(12, 0),
                LocalTime.of(10, 0)
        );
        mockMvc.perform(put("/api/v1/hours/update-opening-hour")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
