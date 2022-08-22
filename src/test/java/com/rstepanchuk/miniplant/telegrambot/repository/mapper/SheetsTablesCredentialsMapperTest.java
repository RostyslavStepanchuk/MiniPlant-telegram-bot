package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetsTableCredentialsEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class SheetsTablesCredentialsMapperTest {
  
  private final SheetsTablesCredentialsMapper subject =
      Mappers.getMapper(SheetsTablesCredentialsMapper.class);

  @Test
  @DisplayName("toModel - copies all values")
  void toModel_shouldCopyAllValues() {
    SheetsTableCredentialsEntity given = new SheetsTableCredentialsEntity();
    Long id = 1L;
    String sheetId = "sheetId";
    String pageName = "pageName";
    String range = "range";
    given.setId(id);
    given.setSheetId(sheetId);
    given.setPageName(pageName);
    given.setRange(range);

    SheetsTableCredentials actual = subject.toModel(given);
    assertEquals(id, actual.getId());
    assertEquals(sheetId, actual.getSheetId());
    assertEquals(pageName, actual.getPageName());
    assertEquals(range, actual.getRange());
  }

  @Test
  @DisplayName("toModel - accepts null")
  void toModel_shouldAcceptNull() {
    assertNull(subject.toModel(null));
  }

  @Test
  @DisplayName("toEntity - copies all values")
  void toEntity_shouldCopyAllValues() {
    SheetsTableCredentials given = new SheetsTableCredentials();
    Long id = 1L;
    String sheetId = "sheetId";
    String pageName = "pageName";
    String range = "range";
    given.setId(id);
    given.setSheetId(sheetId);
    given.setPageName(pageName);
    given.setRange(range);

    SheetsTableCredentialsEntity actual = subject.toEntity(given);
    assertEquals(id, actual.getId());
    assertEquals(sheetId, actual.getSheetId());
    assertEquals(pageName, actual.getPageName());
    assertEquals(range, actual.getRange());
  }

  @Test
  @DisplayName("toEntity - accepts null")
  void toEntity_shouldAcceptNull() {
    assertNull(subject.toEntity(null));
  }
}