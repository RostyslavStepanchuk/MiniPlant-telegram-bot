package com.rstepanchuk.miniplant.telegrambot.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BotUserTest {

  @Test
  @DisplayName("getSheetsTableCredentials - returns empty optional if null")
  void getSheetsTableCredentials_shouldReturnEmptyOptionalIfNull() {
    BotUser subject = new BotUser();
    assertTrue(subject.getSheetsTableCredentials().isEmpty());
  }

  @Test
  @DisplayName("getSheetsTableCredentials - returns optional of credentials")
  void getSheetsTableCredentials_shouldReturnOptionalOfCredentials() {
    BotUser subject = new BotUser();
    SheetsTableCredentials expected = mock(SheetsTableCredentials.class);
    subject.setSheetsCredentials(expected);

    // when & then
    Optional<SheetsTableCredentials> actual = subject.getSheetsTableCredentials();
    assertTrue(actual.isPresent());
    assertEquals(expected, actual.get());
  }
}