package com.rstepanchuk.miniplant.telegrambot.repository;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CANT_SAVE_SHEETS_NOT_CONFIGURED;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.exception.SheetsNotSetUpException;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleServiceFactory;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleSheetsClient;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountingRecordsGoogleSheetsRepo implements AccountingRecordsRepository {

  @VisibleForTesting
  protected static final DateTimeFormatter SHEETS_DATE_FORMAT =
      DateTimeFormatter.ofPattern("dd-MM-yyyy");

  private final GoogleServiceFactory googleServiceFactory;
  private final SheetsTableCredentialsRepositoryImpl tableCredentialsRepo;

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

  @Override
  public AccountingRecord saveRecord(AccountingRecord accountingRecord) {
    Long userId = accountingRecord.getUser().getId();
    SheetsTableCredentials tableCredentials = tableCredentialsRepo
        .findByUserId(userId)
        .orElseThrow(() -> new SheetsNotSetUpException(CANT_SAVE_SHEETS_NOT_CONFIGURED));

    GoogleSheetsClient sheetsService = googleServiceFactory
        .getSheetsService(userId);

    sheetsService.appendRow(tableCredentials, toSheetsRow(accountingRecord));
    return accountingRecord;
  }

  @Override
  public void deleteRecord(AccountingRecord accountingRecord) {
    throw new ApplicationException("googleSheetsRepo.deleteRecord is not implemented");
  }

  @Override
  public AccountingRecord getCurrentRecord(BotUser user) {
    throw new ApplicationException("googleSheetsRepo.deleteRecord is not implemented");
  }
}
