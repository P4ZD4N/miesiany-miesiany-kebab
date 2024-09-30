package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.exceptions.BeverageAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.BeverageNotFoundException;
import com.p4zd4n.kebab.repositories.BeverageRepository;
import com.p4zd4n.kebab.requests.menu.NewBeverageRequest;
import com.p4zd4n.kebab.requests.menu.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.menu.BeverageResponse;
import com.p4zd4n.kebab.responses.menu.NewBeverageResponse;
import com.p4zd4n.kebab.responses.menu.RemovedBeverageResponse;
import com.p4zd4n.kebab.responses.menu.UpdatedBeverageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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

    public Beverage findBeverageByNameAndCapacity(String name, BigDecimal capacity) {

        log.info("Started finding beverage with name '{}' and capacity '{}'", name, capacity);

        Beverage beverage = beverageRepository.findByNameAndCapacity(name, capacity)
                .orElseThrow(() -> new BeverageNotFoundException(name));

        log.info("Successfully found beverage with name '{}' and capacity '{}'", name, capacity);

        return beverage;
    }

    public NewBeverageResponse addBeverage(NewBeverageRequest request) {

        Optional<List<Beverage>> beverages = beverageRepository.findByCapacity(request.newBeverageCapacity());

        if (beverages.isPresent() && !beverages.get().isEmpty()) {

            boolean exists = beverages.get().stream()
                    .anyMatch(beverage ->
                            beverage.getName().equalsIgnoreCase(request.newBeverageName()) &&
                            beverage.getCapacity().compareTo(request.newBeverageCapacity()) == 0
                    );

            if (exists) {
                throw new BeverageAlreadyExistsException();
            }
        }

        log.info("Started adding beverage with name '{}'", request.newBeverageName());

        Beverage newBeverage = Beverage.builder()
                .name(request.newBeverageName())
                .capacity(request.newBeverageCapacity())
                .price(request.newBeveragePrice())
                .build();
        Beverage savedBeverage = beverageRepository.save(newBeverage);
        NewBeverageResponse response = NewBeverageResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully added new beverage with name '" + savedBeverage.getName() + "'")
                        .build();

        log.info("Successfully added new beverage with name '{}'", savedBeverage.getName());

        return response;
    }

    public UpdatedBeverageResponse updateBeverage(Beverage beverage, UpdatedBeverageRequest request) {

        Optional<List<Beverage>> beverages = beverageRepository.findByCapacity(request.updatedBeverageNewCapacity());

        if (beverages.isPresent() && !beverages.get().isEmpty()) {

            boolean exists = beverages.get().stream()
                    .anyMatch(bev ->
                            bev.getName().equalsIgnoreCase(request.updatedBeverageName()) &&
                            bev.getCapacity().compareTo(request.updatedBeverageNewCapacity()) == 0 &&
                            !bev.getId().equals(beverage.getId())
                    );

            if (exists) {
                throw new BeverageAlreadyExistsException();
            }
        }

        UpdatedBeverageResponse response = UpdatedBeverageResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated beverage with name '" + beverage.getName() + "'")
                .build();

        beverage.setName(request.updatedBeverageName());
        beverage.setCapacity(request.updatedBeverageNewCapacity());
        beverage.setPrice(request.updatedBeveragePrice());

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
