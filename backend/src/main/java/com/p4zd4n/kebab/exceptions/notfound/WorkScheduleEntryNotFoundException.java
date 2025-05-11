package com.p4zd4n.kebab.exceptions.notfound;

import lombok.Getter;

@Getter
public class WorkScheduleEntryNotFoundException extends RuntimeException {

    private final Long id;

    public WorkScheduleEntryNotFoundException(Long id) {
        super("Work schedule entry with id '" + id + "' not found!");
        this.id = id;
    }
}
