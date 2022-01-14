package com.rstepanchuk.miniplant.telegrambot.exception;

public class ApplicationException extends RuntimeException {

  public ApplicationException(String message) {
    super(message);
  }
}
