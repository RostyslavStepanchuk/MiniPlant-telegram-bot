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

//  public ValueRange getColumns(String rangeStart, String rangeEnd) throws IOException {
//    return buildGetRequest(rangeStart, rangeEnd)
//        .setMajorDimension("COLUMNS")
//        .execute();
//  }
//
//  public ValueRange getRows(String rangeStart, String rangeEnd) throws IOException {
//    return buildGetRequest(rangeStart, rangeEnd)
//        .execute();
//  }

  public AppendValuesResponse appendRow(SheetPageEntity credentials,
                                        String range, List<Object> values) {
    ValueRange valueRange = new ValueRange();
    valueRange.setValues(List.of(values));
    try {
      return sheets.spreadsheets()
          .values()
          .append(credentials.getSheetId(), credentials.getPageName() + "!" + range, valueRange)
          .setValueInputOption("RAW")
          .execute();
    } catch (IOException e) {
      log.error("Exception when appending row to Google Sheets", e);
      throw new GoogleApiException(CANT_APPEND_ROW);
    }
  }

//  private Sheets.Spreadsheets.Values.Get buildGetRequest(
//      String rangeStart,
//      String rangeEnd) throws IOException {
//    return sheets.spreadsheets()
//        .values()
//        .get(sheetId, "SPREADSHEET" + "!" + rangeStart + ":" + rangeEnd);
//  }

}
