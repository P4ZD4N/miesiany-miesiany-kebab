package com.p4zd4n.kebab.exceptions.notfound;

import com.p4zd4n.kebab.enums.ContactType;
import lombok.Getter;

@Getter
public class ContactNotFoundException extends RuntimeException {

    private final ContactType contactType;

    public ContactNotFoundException(ContactType contactType) {
      super("Contact of type '" + contactType + "' not found!");
      this.contactType = contactType;
    }
}
