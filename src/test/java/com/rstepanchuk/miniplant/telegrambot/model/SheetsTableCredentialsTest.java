package com.rstepanchuk.miniplant.telegrambot.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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

  @Test
  @DisplayName("merge - copies non null values")
  void merge_shouldCopyNonNullValues() {
    long id = 1L;
    String pageName = "pageName";
    String range = "range";
    String sheetId = "sheetId";

    SheetsTableCredentials given = new SheetsTableCredentials();
    given.setId(id);
    given.setSheetId(sheetId);
    given.setPageName(pageName);
    given.setRange(range);

    SheetsTableCredentials subject = new SheetsTableCredentials();
    
    // when
    SheetsTableCredentials actual = subject.merge(given);

    // then
    assertEquals(id, actual.getId());
    assertEquals(sheetId, actual.getSheetId());
    assertEquals(pageName, actual.getPageName());
    assertEquals(range, actual.getRange());
  }

  @Test
  @DisplayName("merge - ignores null and default values")
  void merge_shouldIgnoreNullValues() {
    long id = 1L;
    String pageName = "pageName";
    String range = "range";
    String sheetId = "sheetId";

    SheetsTableCredentials subject = new SheetsTableCredentials();
    subject.setId(id);
    subject.setSheetId(sheetId);
    subject.setPageName(pageName);
    subject.setRange(range);

    SheetsTableCredentials given = new SheetsTableCredentials();

    // when
    SheetsTableCredentials actual = subject.merge(given);

    // then
    assertEquals(id, actual.getId());
    assertEquals(sheetId, actual.getSheetId());
    assertEquals(pageName, actual.getPageName());
    assertEquals(range, actual.getRange());
  }

  @Test
  @DisplayName("merge - returns self")
  void merge_shouldReturnSelf() {
    SheetsTableCredentials subject = new SheetsTableCredentials();
    SheetsTableCredentials given = mock(SheetsTableCredentials.class);

    assertEquals(subject, subject.merge(given));
  }
}