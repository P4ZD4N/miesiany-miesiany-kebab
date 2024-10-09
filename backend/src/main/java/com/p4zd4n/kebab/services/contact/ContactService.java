package com.p4zd4n.kebab.services.contact;

import com.p4zd4n.kebab.entities.Contact;
import com.p4zd4n.kebab.enums.ContactType;
import com.p4zd4n.kebab.exceptions.invalid.InvalidEmailException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidPhoneException;
import com.p4zd4n.kebab.exceptions.notfound.ContactNotFoundException;
import com.p4zd4n.kebab.repositories.ContactRepository;
import com.p4zd4n.kebab.requests.contact.UpdatedContactRequest;
import com.p4zd4n.kebab.responses.contact.ContactResponse;
import com.p4zd4n.kebab.responses.contact.UpdatedContactResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    public Contact findContactByType(ContactType contactType) {

        log.info("Started finding contact of type '{}'", contactType);

        Contact contact = contactRepository.findByContactType(contactType)
                .orElseThrow(() -> new ContactNotFoundException(contactType));

        log.info("Successfully found contact of type '{}'", contactType);

        return contact;
    }

    public UpdatedContactResponse updateContact(Contact contact, UpdatedContactRequest request) {

        UpdatedContactResponse response = UpdatedContactResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated contact of type '" + contact.getContactType() + "'")
                .build();

        if (contact.getContactType().equals(ContactType.TELEPHONE) && request.newValue().length() != 9) {
            throw new InvalidPhoneException(request.newValue());
        }

        if (contact.getContactType().equals(ContactType.EMAIL) && !isEmailValid(request.newValue())) {
            throw new InvalidEmailException(request.newValue());
        }

        contact.setContactType(request.contactType());
        contact.setValue(request.newValue());

        contactRepository.save(contact);

        return response;
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}
