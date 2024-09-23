package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.exceptions.BeverageNotFoundException;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.requests.menu.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.menu.BeverageResponse;
import com.p4zd4n.kebab.responses.menu.RemovedBeverageResponse;
import com.p4zd4n.kebab.responses.menu.UpdatedBeverageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

    public Beverage findBeverageByName(String name) {

        log.info("Started finding beverage with name '{}'", name);

        Beverage beverage = beverageRepository.findByName(name)
                .orElseThrow(() -> new BeverageNotFoundException(name));

        log.info("Successfully found beverage with name '{}'", name);

        return beverage;
    }

    public Beverage saveBeverage(Beverage beverage) {
        log.info("Started saving beverage with name '{}'", beverage.getName());

        Beverage savedBeverage = beverageRepository.save(beverage);

        log.info("Successfully saved beverage with name '{}'", beverage.getName());

        return savedBeverage;
    }

    public UpdatedBeverageResponse updateBeverage(Beverage beverage, UpdatedBeverageRequest request) {

        UpdatedBeverageResponse response = UpdatedBeverageResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated beverage with name '" + beverage.getName() + "'")
                .build();

        beverage.setName(request.name());
        beverage.setCapacity(request.capacity());
        beverage.setPrice(request.price());

        beverageRepository.save(beverage);

        return response;
    }

    public RemovedBeverageResponse removeBeverage(Beverage beverage) {
        log.info("Started removing beverage with name '{}'", beverage.getName());

        beverageRepository.delete(beverage);

        RemovedBeverageResponse response = RemovedBeverageResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully removed beverage with name '" + beverage.getName() + "'")
                        .build();

        log.info("Successfully removed beverage with name '{}'", beverage.getName());

        return response;
    }
}
