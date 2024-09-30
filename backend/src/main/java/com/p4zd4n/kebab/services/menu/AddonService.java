package com.p4zd4n.kebab.services.menu;

import com.p4zd4n.kebab.entities.Addon;
import com.p4zd4n.kebab.exceptions.AddonAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.AddonNotFoundException;
import com.p4zd4n.kebab.repositories.AddonRepository;
import com.p4zd4n.kebab.requests.menu.NewAddonRequest;
import com.p4zd4n.kebab.requests.menu.UpdatedAddonRequest;
import com.p4zd4n.kebab.responses.menu.*;
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

        Optional<Addon> addon = addonRepository.findByName(request.newAddonName());

        if (addon.isPresent()) {
            throw new AddonAlreadyExistsException(request.newAddonName());
        }

        Addon newAddon = Addon.builder()
                .name(request.newAddonName())
                .price(request.newAddonPrice())
                .build();
        Addon savedAddon = addonRepository.save(newAddon);
        NewAddonResponse response = NewAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully added new addon with name '" + savedAddon.getName() + "'")
                .build();

        log.info("Successfully added new addon with name '{}'", savedAddon.getName());

        return response;
    }

    public Addon findAddonByName(String name) {

        log.info("Started finding addon with name '{}'", name);

        Addon addon = addonRepository.findByName(name)
                .orElseThrow(() -> new AddonNotFoundException(name));

        log.info("Successfully found addon with name '{}'", name);

        return addon;
    }

    public UpdatedAddonResponse updateAddon(Addon addon, UpdatedAddonRequest request) {

        UpdatedAddonResponse response = UpdatedAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated addon with name '" + addon.getName() + "'")
                .build();

        addon.setName(request.updatedAddonName());
        addon.setPrice(request.updatedAddonPrice());

        addonRepository.save(addon);

        return response;
    }

    public RemovedAddonResponse removeAddon(Addon addon) {
        log.info("Started removing addon with name '{}'", addon.getName());

        addonRepository.delete(addon);

        RemovedAddonResponse response = RemovedAddonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed addon with name '" + addon.getName() + "'")
                .build();

        log.info("Successfully removed addon with name '{}'", addon.getName());

        return response;
    }
}
