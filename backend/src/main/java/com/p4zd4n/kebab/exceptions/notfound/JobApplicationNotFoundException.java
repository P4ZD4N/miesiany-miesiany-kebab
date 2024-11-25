package com.p4zd4n.kebab.exceptions.notfound;

public class JobApplicationNotFoundException extends RuntimeException {
    public JobApplicationNotFoundException(Long id) {
        super("Job application with id '" + id + "' not found!");
    }
}
