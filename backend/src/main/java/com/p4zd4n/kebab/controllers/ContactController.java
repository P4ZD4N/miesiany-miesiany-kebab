package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.responses.contact.ContactResponse;
import com.p4zd4n.kebab.services.contact.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contact")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactResponse>> getContacts() {
        log.info("Received get contacts request");

        return ResponseEntity.ok(contactService.getContacts());
    }
}
