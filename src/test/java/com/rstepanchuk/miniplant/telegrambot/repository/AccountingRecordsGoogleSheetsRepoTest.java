package com.rstepanchuk.miniplant.telegrambot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.exception.SheetsNotSetUpException;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleServiceFactory;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleSheetsClient;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountingRecordsGoogleSheetsRepoTest {

  @Spy
  @InjectMocks
  private AccountingRecordsGoogleSheetsRepo subject;

  @Mock
  private GoogleServiceFactory googleServiceFactory;

  @Test
  @DisplayName("save - throws exception if user has not sheets credentials ")
  void save_shouldThrowExceptionIfNoSheetsCredentialsForUser() {
    // given
    Long givenUserId = 1L;
    AccountingRecord given = new AccountingRecord();
    BotUser givenUser = new BotUser();
    givenUser.setId(givenUserId);
    givenUser.setSheetsCredentials(null);
    given.setUser(givenUser);


    // when & then
    assertThrows(SheetsNotSetUpException.class, () -> subject.saveRecord(given));
  }

  @Test
  @DisplayName("save - gets GoogleSheets service by User ID")
  void save_shouldGetSheetsServiceUserId() {
    // given
    Long givenUserId = 1L;
    AccountingRecord given = new AccountingRecord();
    BotUser givenUser = new BotUser();
    givenUser.setId(givenUserId);
    givenUser.setSheetsCredentials(mock(SheetsTableCredentials.class));

    given.setUser(givenUser);
    doReturn(mock(GoogleSheetsClient.class))
        .when(googleServiceFactory).getSheetsService(givenUserId);
    doReturn(null)
        .when(subject).toSheetsRow(given);

    // when
    subject.saveRecord(given);
    // then
    verify(googleServiceFactory).getSheetsService(givenUserId);
  }

  @Test
  @DisplayName("save - append data to sheet row")
  void save_shouldAppendDataToSheetsRow() {
    // given
    Long givenUserId = 1L;
    SheetsTableCredentials credentials = mock(SheetsTableCredentials.class);
    AccountingRecord given = new AccountingRecord();
    BotUser givenUser = new BotUser();
    givenUser.setId(givenUserId);
    givenUser.setSheetsCredentials(credentials);
    given.setUser(givenUser);
    List<Object> rowDataMock = List.of("testValue");
    GoogleSheetsClient sheetsServiceMock = mock(GoogleSheetsClient.class);

    doReturn(sheetsServiceMock)
        .when(googleServiceFactory).getSheetsService(givenUserId);
    doReturn(rowDataMock)
        .when(subject).toSheetsRow(given);

    // when
    AccountingRecord actual = subject.saveRecord(given);
    // then
    assertEquals(given, actual);
    verify(sheetsServiceMock).appendRow(credentials, rowDataMock);
    verify(subject).toSheetsRow(given);

  }

  @Test
  @DisplayName("toSheetsRow - sets current date as first value")
  void toSheetsRow_shouldSetCurrentDateAsFirstValue() {
    // given
    String stringDummy = "dummy";
    long numberDummy = 0;
    AccountingRecord givenRecord = mock(AccountingRecord.class);
    doReturn(stringDummy).when(givenRecord).getType();
    doReturn(stringDummy).when(givenRecord).getAccount();
    doReturn(stringDummy).when(givenRecord).getCategory();
    doReturn(BigDecimal.valueOf(numberDummy)).when(givenRecord).getAmount();
    // when
    List<Object> actual = subject.toSheetsRow(givenRecord);
    // then
    Object actualString = actual.get(0);
    assertEquals(String.class, actualString.getClass());
    LocalDate actualDate = LocalDate.parse(
        String.valueOf(actualString),
        AccountingRecordsGoogleSheetsRepo.SHEETS_DATE_FORMAT);
    assertEquals(LocalDate.now(), actualDate);
  }

  @Test
  @DisplayName("toSheetsRow - returns accounting record as list of values")
  void toSheetsRow_shouldReturnAccountingDataAsListOfValues() {
    // given
    String givenType = "приход";
    String givenAccount = "нал";
    String givenCategory = "Горшки";
    BigDecimal givenAmount = BigDecimal.valueOf(17.5);
    AccountingRecord givenRecord = mock(AccountingRecord.class);
    doReturn(givenType).when(givenRecord).getType();
    doReturn(givenAccount).when(givenRecord).getAccount();
    doReturn(givenCategory).when(givenRecord).getCategory();
    doReturn(givenAmount).when(givenRecord).getAmount();

    // when
    List<Object> actual = subject.toSheetsRow(givenRecord);
    // then
    assertEquals(actual.get(1), givenType);
    assertEquals(actual.get(2), givenAmount);
    assertEquals(actual.get(3), givenAccount);
    assertEquals(actual.get(4), givenCategory);
  }

  @Test
  @DisplayName("toSheetsRow - doesn't accept null values in record")
  void toSheetsRow_shouldThrowExceptionIfRecordHasNullValues() {
    // given
    String givenType = "приход";
    String givenAccount = "нал";
    String givenCategory = "Горшки";
    BigDecimal givenAmount = BigDecimal.valueOf(17.5);
    AccountingRecord basicRecord = mock(AccountingRecord.class);
    doReturn(givenAccount).when(basicRecord).getAccount();
    doReturn(givenCategory).when(basicRecord).getCategory();
    doReturn(givenAmount).when(basicRecord).getAmount();

    // when & then
    // no type
    assertThrows(NullPointerException.class, () -> subject.toSheetsRow(basicRecord));

    // no amount
    doReturn(givenType).when(basicRecord).getType();
    doReturn(null).when(basicRecord).getAmount();
    assertThrows(NullPointerException.class, () -> subject.toSheetsRow(basicRecord));

    // no account
    doReturn(givenAmount).when(basicRecord).getAmount();
    doReturn(null).when(basicRecord).getAccount();
    assertThrows(NullPointerException.class, () -> subject.toSheetsRow(basicRecord));

    // no category
    doReturn(givenCategory).when(basicRecord).getAccount();
    doReturn(null).when(basicRecord).getCategory();
    assertThrows(NullPointerException.class, () -> subject.toSheetsRow(basicRecord));
  }

}