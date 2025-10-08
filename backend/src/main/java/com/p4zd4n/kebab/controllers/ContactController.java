package com.p4zd4n.kebab.controllers;

import com.p4zd4n.kebab.entities.Contact;
import com.p4zd4n.kebab.requests.contact.UpdatedContactRequest;
import com.p4zd4n.kebab.responses.contact.ContactResponse;
import com.p4zd4n.kebab.responses.contact.UpdatedContactResponse;
import com.p4zd4n.kebab.services.contact.ContactService;
import com.p4zd4n.kebab.utils.LanguageValidator;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @PutMapping("/update-contact")
  public ResponseEntity<UpdatedContactResponse> updateContact(
      @RequestHeader(value = "Accept-Language") String language,
      @Valid @RequestBody UpdatedContactRequest request) {
    LanguageValidator.validateLanguage(language);

    log.info("Received update contact request");

    Contact existingContact = contactService.findContactByType(request.contactType());
    UpdatedContactResponse response = contactService.updateContact(existingContact, request);

    log.info("Successfully updated contact of type: {}", existingContact.getContactType());

    return ResponseEntity.ok(response);
  }
}
