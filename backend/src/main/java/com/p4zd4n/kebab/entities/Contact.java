package com.p4zd4n.kebab.entities;

import com.p4zd4n.kebab.enums.ContactType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
public class Contact extends WithTimestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "contact_type", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    @Column(name = "value", nullable = false)
    private String value;

    @Builder
    public Contact(ContactType contactType, String value) {
        this.contactType = contactType;
        this.value = value;
    }
}
