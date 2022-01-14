package com.rstepanchuk.miniplant.telegrambot.exception;

public class MessageValidationException extends ApplicationException {
  public MessageValidationException(String message) {
    super(message);
  }
}
