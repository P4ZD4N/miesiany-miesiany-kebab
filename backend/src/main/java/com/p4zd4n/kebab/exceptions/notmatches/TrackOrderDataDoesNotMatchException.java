package com.p4zd4n.kebab.exceptions.notmatches;

import lombok.Getter;

@Getter
public class TrackOrderDataDoesNotMatchException extends RuntimeException {
    
    private final Long id;
    private final String customerPhone;
    
    public TrackOrderDataDoesNotMatchException(Long id, String customerPhone) {
        super("Entered ID (" + id + ") and customer phone number (" + customerPhone + ") do not match any existing order!");
        this.id = id;
        this.customerPhone = customerPhone;
    }
}
