package com.rstepanchuk.miniplant.telegrambot.google;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CANT_APPEND_ROW;

import java.io.IOException;
import java.util.List;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetPageEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GoogleSheetsClient {

  private final Sheets sheets;

  public AppendValuesResponse appendRow(SheetPageEntity credentials, List<Object> rowValues) {
    ValueRange valueRange = new ValueRange();
    valueRange.setValues(List.of(rowValues));
    try {
      return sheets.spreadsheets()
          .values()
          .append(credentials.getSheetId(),
              credentials.getPageName() + "!" + credentials.getRange(),
              valueRange)
          .setValueInputOption("RAW")
          .execute();
    } catch (IOException e) {
      log.error("Exception when appending row to Google Sheets", e);
      throw new GoogleApiException(CANT_APPEND_ROW);
    }
  }

}
