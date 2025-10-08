package com.p4zd4n.kebab.entities.interfaces;

import com.p4zd4n.kebab.utils.interfaces.Observer;
import jakarta.mail.MessagingException;

public interface Observable {

  void registerObserver(Observer observer);

  void unregisterObserver(Observer observer);

  void notifyObservers() throws MessagingException;
}
