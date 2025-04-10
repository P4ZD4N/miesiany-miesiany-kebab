package com.p4zd4n.kebab.exceptions.expired;

public class TrackOrderExpiredException extends RuntimeException {
    public TrackOrderExpiredException() {
        super("Tracking for this order is no longer available, because last update was over 40 minutes ago!");
    }
}
