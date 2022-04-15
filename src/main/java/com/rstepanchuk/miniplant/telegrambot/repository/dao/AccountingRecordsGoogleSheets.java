package com.rstepanchuk.miniplant.telegrambot.repository.dao;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.SHEETS_NOT_SET_UP;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.exception.SheetsNotSetUpException;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleServiceFactory;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleSheetsClient;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetPageEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountingRecordsGoogleSheets {

  @VisibleForTesting
  protected static final DateTimeFormatter SHEETS_DATE_FORMAT =
      DateTimeFormatter.ofPattern("dd-MM-yyyy");

  private final GoogleServiceFactory googleServiceFactory;
  private final SheetsPageDao sheetsPageDao;

  public AccountingRecord save(AccountingRecord accountingRecord) {

    Long userId = accountingRecord.getUser().getId();
    SheetPageEntity sheetPage = sheetsPageDao.findByUserIdEquals(userId)
        .orElseThrow(() -> new SheetsNotSetUpException(SHEETS_NOT_SET_UP));

    GoogleSheetsClient sheetsService = googleServiceFactory
        .getSheetsService(userId);

    sheetsService.appendRow(sheetPage, toSheetsRow(accountingRecord));
    return accountingRecord;
  }

  @VisibleForTesting
  protected List<Object> toSheetsRow(AccountingRecord accountingRecord) {
    LocalDate entered = LocalDate.now();
    return List.of(
        entered.format(SHEETS_DATE_FORMAT),
        accountingRecord.getType(),
        accountingRecord.getAmount(),
        accountingRecord.getAccount(),
        accountingRecord.getCategory());
  }
}
