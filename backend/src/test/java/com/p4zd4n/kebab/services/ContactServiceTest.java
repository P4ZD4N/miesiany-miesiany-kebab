package com.p4zd4n.kebab.services;

import com.p4zd4n.kebab.entities.Contact;
import com.p4zd4n.kebab.enums.ContactType;
import com.p4zd4n.kebab.exceptions.invalid.InvalidPhoneException;
import com.p4zd4n.kebab.exceptions.notfound.ContactNotFoundException;
import com.p4zd4n.kebab.repositories.ContactRepository;
import com.p4zd4n.kebab.requests.contact.UpdatedContactRequest;
import com.p4zd4n.kebab.responses.contact.ContactResponse;
import com.p4zd4n.kebab.responses.contact.UpdatedContactResponse;
import com.p4zd4n.kebab.services.contact.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getContacts_ShouldReturnContacts_WhenCalled() {

        List<Contact> contactList = Arrays.asList(
                Contact.builder()
                        .contactType(ContactType.EMAIL)
                        .value("email@example.com")
                        .build(),
                Contact.builder()
                        .contactType(ContactType.TELEPHONE)
                        .value("123456789")
                        .build()
        );

        when(contactRepository.findAll()).thenReturn(contactList);

        List<ContactResponse> result = contactService.getContacts();

        assertEquals(2, result.size());

        assertEquals(ContactType.EMAIL, result.getFirst().contactType());
        assertEquals("email@example.com", result.getFirst().value());

        assertEquals(ContactType.TELEPHONE, result.getLast().contactType());
        assertEquals("123456789", result.getLast().value());

        verify(contactRepository, times(1)).findAll();
    }

    @Test
    public void findByContactType_ShouldReturnContact_WhenContactExists() {

        Contact contact = Contact.builder()
                .contactType(ContactType.EMAIL)
                .value("email@example.com")
                .build();

        when(contactRepository.findByContactType(ContactType.EMAIL)).thenReturn(Optional.of(contact));

        Contact foundContact = contactService.findContactByType(ContactType.EMAIL);

        assertNotNull(foundContact);
        assertEquals(ContactType.EMAIL, foundContact.getContactType());
        assertEquals("email@example.com", foundContact.getValue());

        verify(contactRepository, times(1)).findByContactType(ContactType.EMAIL);
    }

    @Test
    public void findByContactType_ShouldThrowContactNotFoundException_WhenContactNotExists() {

        when(contactRepository.findByContactType(ContactType.EMAIL)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> {
            contactService.findContactByType(ContactType.EMAIL);
        });

        verify(contactRepository, times(1)).findByContactType(ContactType.EMAIL);
    }

    @Test
    public void updateContact_ShouldUpdateEmail_WhenNewEmailValid() {

        Contact existingContact = Contact.builder()
                .contactType(ContactType.EMAIL)
                .value("email@example.com")
                .build();

        UpdatedContactRequest request = UpdatedContactRequest.builder()
                .contactType(ContactType.EMAIL)
                .newValue("email@gmail.com")
                .build();

        when(contactRepository.save(any(Contact.class))).thenReturn(existingContact);

        UpdatedContactResponse response = contactService.updateContact(existingContact, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals("Successfully updated contact of type 'EMAIL'", response.message());
        assertEquals("email@gmail.com", existingContact.getValue());

        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    public void updateContact_ShouldThrowInvalidPhoneException_WhenNewTelephoneInvalid() {

        Contact existingContact = Contact.builder()
                .contactType(ContactType.TELEPHONE)
                .value("123456789")
                .build();

        UpdatedContactRequest request = UpdatedContactRequest.builder()
                .contactType(ContactType.TELEPHONE)
                .newValue("123AAA123")
                .build();

        assertThrows(InvalidPhoneException.class, () -> {
            contactService.updateContact(existingContact, request);
        });
    }
}
