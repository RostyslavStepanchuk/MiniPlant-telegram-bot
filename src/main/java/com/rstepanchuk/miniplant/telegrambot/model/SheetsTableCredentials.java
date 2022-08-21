package com.rstepanchuk.miniplant.telegrambot.model;

import java.util.Optional;
import lombok.Data;

@Data
public class SheetsTableCredentials {

  private static final String DEFAULT_RANGE = "C1:C2";

  Long id;
  String sheetId;
  String pageName;
  String range = DEFAULT_RANGE;

  public String getTableFullAddress() {
    return pageName + "!" + range;
  }

  public SheetsTableCredentials merge(SheetsTableCredentials incoming) {
    Optional.ofNullable(incoming.getId()).ifPresent(this::setId);
    Optional.ofNullable(incoming.getSheetId()).ifPresent(this::setSheetId);
    Optional.ofNullable(incoming.getPageName()).ifPresent(this::setPageName);
    if (!DEFAULT_RANGE.equals(incoming.getRange())) {
      range = incoming.getRange();
    }
    return this;
  }
}
