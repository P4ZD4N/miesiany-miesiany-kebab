package com.p4zd4n.kebab.exceptions.alreadyexists;

import lombok.Getter;

@Getter
public class JobOfferAlreadyExistsException extends RuntimeException {

    private final String positionName;

    public JobOfferAlreadyExistsException(String positionName) {
        super("Job offer with position name '" + positionName + "' already exists!");
        this.positionName = positionName;
    }
}
