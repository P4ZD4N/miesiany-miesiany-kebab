package com.p4zd4n.kebab.services.contact;

import com.p4zd4n.kebab.entities.Contact;
import com.p4zd4n.kebab.repositories.ContactRepository;
import com.p4zd4n.kebab.responses.contact.ContactResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<ContactResponse> getContacts() {

        log.info("Started retrieving contacts");

        List<Contact> contacts = contactRepository.findAll();

        List<ContactResponse> response = contacts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Successfully retrieved contacts");

        return response;
    }

    private ContactResponse mapToResponse(Contact contact) {

        return ContactResponse.builder()
                .contactType(contact.getContactType())
                .value(contact.getValue())
                .build();
    }
}
