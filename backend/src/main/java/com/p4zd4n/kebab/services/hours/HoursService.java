package com.p4zd4n.kebab.services.hours;

import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.repositories.OpeningHoursRepository;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HoursService {

    private final OpeningHoursRepository openingHourRepository;

    public HoursService(OpeningHoursRepository openingHourRepository) {
        this.openingHourRepository = openingHourRepository;
    }

    public List<OpeningHoursResponse> getOpeningHours() {

        List<OpeningHour> openingHours = openingHourRepository.findAll();

        return openingHours.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OpeningHoursResponse mapToResponse(OpeningHour openingHour) {

        return OpeningHoursResponse.builder()
                .dayOfWeek(openingHour.getDayOfWeek())
                .openingTime(openingHour.getOpeningTime())
                .closingTime(openingHour.getClosingTime())
                .build();
    }
}
