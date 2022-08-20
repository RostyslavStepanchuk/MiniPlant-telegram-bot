package com.rstepanchuk.miniplant.telegrambot.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SheetsTableCredentialsTest {

  @Test
  @DisplayName("getTableFullAddress - should create range from page and range")
  void getTableFullAddress_shouldCombinePageNameAndRange() {
    String pageName = "pageName";
    String range = "range";
    String expected = pageName + "!" + range;

    SheetsTableCredentials subject = new SheetsTableCredentials();
    subject.setPageName(pageName);
    subject.setRange(range);

    assertEquals(expected, subject.getTableFullAddress());
  }
}