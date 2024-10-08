package com.p4zd4n.kebab.repositories;

import com.p4zd4n.kebab.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
