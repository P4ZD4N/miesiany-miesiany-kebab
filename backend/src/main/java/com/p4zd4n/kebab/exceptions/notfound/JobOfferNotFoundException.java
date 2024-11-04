package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class JobOfferNotFoundException extends RuntimeException {

    private final String positionName;

    public JobOfferNotFoundException(String positionName) {
        super("Job offer with position name '" + positionName + "' not found!");
        this.positionName = positionName;
    }
}
