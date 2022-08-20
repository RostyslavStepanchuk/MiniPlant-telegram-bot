package com.rstepanchuk.miniplant.telegrambot.model;

import lombok.Data;

@Data
public class SheetsTableCredentials {
  Long id;
  String sheetId;
  String pageName;
  String range;

  public String getTableFullAddress() {
    return pageName + "!" + range;
  }
}
