package com.p4zd4n.kebab.services.hours;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.enums.DayOfWeek;
import com.p4zd4n.kebab.repositories.OpeningHoursRepository;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HoursService {

    private final OpeningHoursRepository openingHourRepository;

    private final List<DayOfWeek> daysOfWeekOrder = Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
    );

    public HoursService(OpeningHoursRepository openingHourRepository) {
        this.openingHourRepository = openingHourRepository;
    }

    public List<OpeningHoursResponse> getOpeningHours() {

        log.info("Started retrieving opening hours");

        List<OpeningHour> openingHours = openingHourRepository.findAll();

        openingHours.sort(Comparator.comparingInt(o -> daysOfWeekOrder.indexOf(o.getDayOfWeek())));

        List<OpeningHoursResponse> response = openingHours.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved opening hours");

        return response;
    }

    private OpeningHoursResponse mapToResponse(OpeningHour openingHour) {

        return OpeningHoursResponse.builder()
                .dayOfWeek(openingHour.getDayOfWeek())
                .openingTime(openingHour.getOpeningTime())
                .closingTime(openingHour.getClosingTime())
                .build();
    }

    public OpeningHour findOpeningHourByDayOfWeek(DayOfWeek dayOfWeek) {

        log.info("Started finding opening hours on {}", dayOfWeek);

        Optional<OpeningHour> optionalOpeningHour = openingHourRepository.findByDayOfWeek(dayOfWeek);

        log.info("Successfully found opening hours on {}", dayOfWeek);

        return optionalOpeningHour.orElse(null);
    }

    public OpeningHour saveOpeningHour(OpeningHour openingHour) {

        log.info("Started saving opening hours on {}", openingHour.getDayOfWeek());

        OpeningHour savedOpeningHour = openingHourRepository.save(openingHour);

        log.info("Successfully saved opening hours on {}", openingHour.getDayOfWeek());

        return savedOpeningHour;
    }
}