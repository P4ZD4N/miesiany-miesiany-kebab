package com.p4zd4n.kebab.exceptions.notfound;

public class JobApplicationNotFound extends RuntimeException {
    public JobApplicationNotFound(Long id) {
        super("Job application with id '" + id + "' not found!");
    }
}
