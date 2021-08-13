package com.rstepanchuk.miniplant.telegrambot.bot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BotUserFilterTest {

  private BotUserFilter botUserFilter;

  @Test
  void name() {
    assertThrows(NullPointerException.class, ()-> botUserFilter.processUpdate(null));
  }
}