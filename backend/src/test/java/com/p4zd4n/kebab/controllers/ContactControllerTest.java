package com.p4zd4n.kebab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p4zd4n.kebab.entities.Beverage;
import com.p4zd4n.kebab.entities.Contact;
import com.p4zd4n.kebab.enums.ContactType;
import com.p4zd4n.kebab.exceptions.GlobalExceptionHandler;
import com.p4zd4n.kebab.exceptions.alreadyexists.BeverageAlreadyExistsException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidEmailException;
import com.p4zd4n.kebab.exceptions.invalid.InvalidPhoneException;
import com.p4zd4n.kebab.requests.contact.UpdatedContactRequest;
import com.p4zd4n.kebab.requests.menu.beverages.UpdatedBeverageRequest;
import com.p4zd4n.kebab.responses.contact.ContactResponse;
import com.p4zd4n.kebab.responses.contact.UpdatedContactResponse;
import com.p4zd4n.kebab.responses.menu.beverages.UpdatedBeverageResponse;
import com.p4zd4n.kebab.services.contact.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ContactControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @InjectMocks
    private ContactController contactController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getContacts_ShouldReturnContacts_WhenCalled() throws Exception {

        List<ContactResponse> contactResponsesList = Arrays.asList(
                ContactResponse.builder()
                        .contactType(ContactType.TELEPHONE)
                        .value("123456789")
                        .build(),
                ContactResponse.builder()
                        .contactType(ContactType.EMAIL)
                        .value("email@example.com")
                        .build()
        );

        when(contactService.getContacts()).thenReturn(contactResponsesList);

        mockMvc.perform(get("/api/v1/contact/contacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].value", is("123456789")))
                .andExpect(jsonPath("$[1].value", is("email@example.com")));

        verify(contactService, times(1)).getContacts();
    }

    @Test
    public void updateContact_ShouldReturnOk_WhenValidRequest() throws Exception {

        Contact existingContact = Contact.builder()
                .contactType(ContactType.TELEPHONE)
                .value("123456789")
                .build();

        UpdatedContactRequest request = UpdatedContactRequest.builder()
                .contactType(ContactType.TELEPHONE)
                .newValue("321654987")
                .build();

        UpdatedContactResponse expectedResponse = UpdatedContactResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated contact of type '" + existingContact.getContactType() + "'")
                .build();

        when(contactService.findContactByType(request.contactType())).thenReturn(existingContact);
        when(contactService.updateContact(any(Contact.class), any(UpdatedContactRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/contact/update-contact")
                .header("Accept-Language", "en")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status_code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.message", is("Successfully updated contact of type '" + existingContact.getContactType() + "'")));

        verify(contactService, times(1)).findContactByType(request.contactType());
        verify(contactService, times(1)).updateContact(existingContact, request);
    }

    @Test
    public void updateContact_ShouldReturnBadRequest_WhenInvalidPhone() throws Exception {

        Contact existingContact = Contact.builder()
                .contactType(ContactType.TELEPHONE)
                .value("123456789")
                .build();

        UpdatedContactRequest request = UpdatedContactRequest.builder()
                .contactType(ContactType.TELEPHONE)
                .newValue("321aaa987")
                .build();

        when(contactService.findContactByType(request.contactType())).thenReturn(existingContact);
        when(contactService.updateContact(existingContact, request)).thenThrow(new InvalidPhoneException(request.newValue()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("phone.invalidFormat", null, Locale.forLanguageTag("pl")))
                .thenReturn("Nieprawidlowy format telefonu!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ContactController(contactService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/contact/update-contact")
                .header("Accept-Language", "pl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status_code", is(400)))
                .andExpect(jsonPath("$.message", is("Nieprawidlowy format telefonu!")));

        verify(contactService, times(1)).updateContact(existingContact, request);
    }

    @Test
    public void updateContact_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {

        Contact existingContact = Contact.builder()
                .contactType(ContactType.EMAIL)
                .value("email@example.com")
                .build();

        UpdatedContactRequest request = UpdatedContactRequest.builder()
                .contactType(ContactType.EMAIL)
                .newValue("email@example")
                .build();

        when(contactService.findContactByType(request.contactType())).thenReturn(existingContact);
        when(contactService.updateContact(existingContact, request)).thenThrow(new InvalidEmailException(request.newValue()));

        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage("email.invalidFormat", null, Locale.forLanguageTag("pl")))
                .thenReturn("Nieprawidlowy format adresu email!");

        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ContactController(contactService))
                .setControllerAdvice(exceptionHandler)
                .build();

        mockMvc.perform(put("/api/v1/contact/update-contact")
                .header("Accept-Language", "pl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status_code", is(400)))
                .andExpect(jsonPath("$.message", is("Nieprawidlowy format adresu email!")));

        verify(contactService, times(1)).updateContact(existingContact, request);
    }

    @Test
    public void updateContact_ShouldReturnBadRequest_WhenMissingHeader() throws Exception {

        UpdatedContactRequest request = UpdatedContactRequest.builder()
                .contactType(ContactType.TELEPHONE)
                .newValue("321111987")
                .build();

        mockMvc.perform(put("/api/v1/contact/update-contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateContact_ShouldReturnBadRequest_WhenInvalidHeader() throws Exception {

        String[] invalidHeaders = {"fr", "ES", "ENG", "RuS", "GER", "Sw", "aa", ""};

        for (String header : invalidHeaders) {
            mockMvc.perform(put("/api/v1/contact/update-contact")
                    .header("Accept-Language", header))
                    .andExpect(status().isBadRequest());
        }
    }
}
