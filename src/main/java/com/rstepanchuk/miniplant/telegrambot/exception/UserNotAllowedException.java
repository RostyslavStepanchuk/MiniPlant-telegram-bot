package com.rstepanchuk.miniplant.telegrambot.exception;

public class UserNotAllowedException extends ApplicationException {
  public UserNotAllowedException(String message) {
    super(message);
  }
}
