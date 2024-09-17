package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.OpeningHour;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.responses.hours.OpeningHoursResponse;
import com.p4zd4n.kebab.responses.menu.BeverageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BeverageService {

    private final BeverageRepository beverageRepository;

    public BeverageService(BeverageRepository beverageRepository) {
        this.beverageRepository = beverageRepository;
    }

    public List<BeverageResponse> getBeverages() {

        log.info("Started retrieving beverages");

        List<Beverage> beverages = beverageRepository.findAll();

        List<BeverageResponse> response = beverages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved beverages");

        return response;
    }

    private BeverageResponse mapToResponse(Beverage beverage) {

        return BeverageResponse.builder()
                .name(beverage.getName())
                .capacity(beverage.getCapacity())
                .price(beverage.getPrice())
                .build();
    }

    public void save(Beverage beverage) {
        beverageRepository.save(beverage);
    }
}
