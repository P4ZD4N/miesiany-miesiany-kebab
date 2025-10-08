package com.p4zd4n.kebab.utils.interfaces;

import com.p4zd4n.kebab.entities.interfaces.Promotion;
import jakarta.mail.MessagingException;

public interface Observer {
  void update(Promotion promotion) throws MessagingException;
}
