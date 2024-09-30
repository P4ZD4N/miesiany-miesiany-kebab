package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.exceptions.AddonAlreadyExistsException;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.requests.menu.NewAddonRequest;
import com.p4zd4n.kebab.responses.menu.AddonResponse;
import com.p4zd4n.kebab.responses.menu.NewAddonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddonService {

    private final AddonRepository addonRepository;

    public AddonService(AddonRepository addonRepository) {
        this.addonRepository = addonRepository;
    }

    public List<AddonResponse> getAddons() {

        log.info("Started retrieving addons");

        List<Addon> addons = addonRepository.findAll();

        List<AddonResponse> response = addons.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved addons");

        return response;
    }

    private AddonResponse mapToResponse(Addon addon) {

        return AddonResponse.builder()
                .name(addon.getName())
                .price(addon.getPrice())
                .build();
    }

    public NewAddonResponse addAddon(NewAddonRequest request) {

        Optional<Addon> addon = addonRepository.findByName(request.name());

        if (addon.isPresent()) {
            throw new AddonAlreadyExistsException();
        }

        Addon newAddon = Addon.builder()
                .name(request.name())
                .price(request.price())
                .build();
        Addon savedAddon = addonRepository.save(newAddon);
        NewAddonResponse response = NewAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new addon with name '" + savedAddon.getName() + "'")
                .build();

        log.info("Successfully added new addon with name '{}'", savedAddon.getName());

        return response;
    }
}
