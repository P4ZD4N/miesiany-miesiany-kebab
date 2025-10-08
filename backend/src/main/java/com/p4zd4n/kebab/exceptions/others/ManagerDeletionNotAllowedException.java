package com.p4zd4n.kebab.exceptions.others;

public class ManagerDeletionNotAllowedException extends RuntimeException {

  public ManagerDeletionNotAllowedException() {
    super("Deleting a manager is not allowed!");
  }
}
