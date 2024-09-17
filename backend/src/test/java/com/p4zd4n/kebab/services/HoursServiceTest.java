package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.enums.DayOfWeek;
import com.p4zd4n.kebab.repositories.OpeningHoursRepository;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.services.hours.HoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class HoursServiceTest {

    @Mock
    private OpeningHoursRepository openingHoursRepository;

    @InjectMocks
    private HoursService hoursService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getOpeningHours_ShouldReturnOpeningHours_WhenCalled() {

        List<OpeningHour> openingHoursList = Arrays.asList(
                new OpeningHour(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0)),
                new OpeningHour(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(15, 0))
        );

        when(openingHoursRepository.findAll()).thenReturn(openingHoursList);

        List<OpeningHoursResponse> result = hoursService.getOpeningHours();

        assertEquals(2, result.size());

        assertEquals(DayOfWeek.MONDAY, result.getFirst().dayOfWeek());
        assertEquals(LocalTime.of(9, 0), result.getFirst().openingTime());
        assertEquals(LocalTime.of(18, 0), result.getFirst().closingTime());

        assertEquals(DayOfWeek.TUESDAY, result.get(1).dayOfWeek());
        assertEquals(LocalTime.of(10, 0), result.get(1).openingTime());
        assertEquals(LocalTime.of(15, 0), result.get(1).closingTime());

        verify(openingHoursRepository, times(1)).findAll();
    }

    @Test
    public void findByDayOfWeek_ShouldReturnOpeningHourByDayOfWeek_WhenCalled() {

        OpeningHour openingHour = new OpeningHour(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(19, 0));

        when(openingHoursRepository.findByDayOfWeek(DayOfWeek.MONDAY)).thenReturn(Optional.of(openingHour));

        OpeningHour foundOpeningHour = hoursService.findOpeningHourByDayOfWeek(DayOfWeek.MONDAY);

        assertNotNull(foundOpeningHour);
        assertEquals(DayOfWeek.MONDAY, foundOpeningHour.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), foundOpeningHour.getOpeningTime());
        assertEquals(LocalTime.of(19, 0), foundOpeningHour.getClosingTime());

        verify(openingHoursRepository, times(1)).findByDayOfWeek(DayOfWeek.MONDAY);
    }

    @Test
    public void saveOpeningHour_ShouldSaveOpeningHour_WhenCalled() {

        OpeningHour openingHour = new OpeningHour(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0));

        when(openingHoursRepository.save(openingHour)).thenReturn(openingHour);

        OpeningHour result = hoursService.saveOpeningHour(openingHour);

        assertNotNull(result);
        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), result.getOpeningTime());
        assertEquals(LocalTime.of(18, 0), result.getClosingTime());

        verify(openingHoursRepository, times(1)).save(openingHour);
    }
}
