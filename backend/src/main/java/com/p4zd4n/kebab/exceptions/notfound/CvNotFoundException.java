package com.p4zd4n.kebab.exceptions.notfound;

public class CvNotFoundException extends RuntimeException {
    public CvNotFoundException(Long id) {
        super("CV with id '" + id + "' not found!");
    }
}
