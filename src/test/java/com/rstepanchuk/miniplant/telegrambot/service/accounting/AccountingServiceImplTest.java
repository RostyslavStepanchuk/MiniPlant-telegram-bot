package com.rstepanchuk.miniplant.telegrambot.service.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountingServiceImplTest {

  @InjectMocks
  AccountingServiceImpl subject;

  @Mock
  AccountingRecordsRepository accountingRecordsRepository;

  @Test
  @DisplayName("updateAccountingRecord - retrieves record from repository")
  void updateAccountingRecord_shouldRetrieveCurrentRecordFromRepository() {
    // given
    BotUser givenUser = new BotUser();
    AccountingRecord given = new AccountingRecord();
    given.setUser(givenUser);
    doReturn(mock(AccountingRecord.class))
        .when(accountingRecordsRepository).getCurrentRecord(givenUser);
    // when
    subject.updateAccountingRecord(given);
    // then
    verify(accountingRecordsRepository).getCurrentRecord(givenUser);
  }

  @Test
  @DisplayName("updateAccountingRecord - copies all non-null values")
  void updateAccountingRecord_shouldCopyAllNonNullValues() {
    // given
    String recordType = "type";
    BigDecimal recordAmount = BigDecimal.valueOf(17.25);
    String recordAccount = "account";
    String recordCategory = "category";

    AccountingRecord given = new AccountingRecord();
    given.setType(recordType);
    given.setAmount(recordAmount);
    given.setAccount(recordAccount);
    given.setCategory(recordCategory);

    BotUser givenUser = new BotUser();
    given.setUser(givenUser);

    AccountingRecord actual = new AccountingRecord();
    doReturn(actual)
        .when(accountingRecordsRepository).getCurrentRecord(givenUser);

    // when
    subject.updateAccountingRecord(given);

    // then
    assertEquals(recordType, actual.getType());
    assertEquals(recordAmount, actual.getAmount());
    assertEquals(recordAccount, actual.getAccount());
    assertEquals(recordCategory, actual.getCategory());
  }

  @Test
  @DisplayName("updateAccountingRecord - ignores all null values")
  void updateAccountingRecord_shouldIgnoreAllNullValues() {
    // given
    String recordType = "type";
    BigDecimal recordAmount = BigDecimal.valueOf(17.25);
    String recordAccount = "account";
    String recordCategory = "category";

    AccountingRecord given = new AccountingRecord();
    BotUser givenUser = new BotUser();
    given.setUser(givenUser);

    AccountingRecord actual = new AccountingRecord();
    actual.setType(recordType);
    actual.setAmount(recordAmount);
    actual.setAccount(recordAccount);
    actual.setCategory(recordCategory);

    doReturn(actual)
        .when(accountingRecordsRepository).getCurrentRecord(givenUser);

    // when
    subject.updateAccountingRecord(given);

    // then
    assertEquals(recordType, actual.getType());
    assertEquals(recordAmount, actual.getAmount());
    assertEquals(recordAccount, actual.getAccount());
    assertEquals(recordCategory, actual.getCategory());
  }

  @Test
  @DisplayName("updateAccountingRecord - saves updated record to repository")
  void updateAccountingRecord_shouldSaveUpdatedRecordToRepository() {
    // given
    BotUser givenUser = new BotUser();
    AccountingRecord given = new AccountingRecord();
    given.setUser(givenUser);
    AccountingRecord currentRecord = mock(AccountingRecord.class);
    AccountingRecord updatedRecord = mock(AccountingRecord.class);

    doReturn(currentRecord)
        .when(accountingRecordsRepository).getCurrentRecord(givenUser);
    doReturn(updatedRecord).when(currentRecord).merge(given);
    doReturn(updatedRecord)
        .when(accountingRecordsRepository).saveRecord(any());

    // when
    AccountingRecord actual = subject.updateAccountingRecord(given);

    // then
    verify(accountingRecordsRepository).saveRecord(updatedRecord);
    assertEquals(updatedRecord, actual);
  }


  @Test
  @DisplayName("deleteCurrentRecord - gets current record from repo")
  void deleteCurrentRecord_shouldGetCurrentRecordFromRepository() {
    BotUser given = new BotUser();
    subject.deleteCurrentRecord(given);
    verify(accountingRecordsRepository).getCurrentRecord(given);
  }

  @Test
  @DisplayName("deleteCurrentRecord - deletes record")
  void deleteCurrentRecord_shouldDeleteRecord() {
    // given
    BotUser given = new BotUser();
    AccountingRecord accountingRecord = mock(AccountingRecord.class);

    doReturn(accountingRecord).when(accountingRecordsRepository).getCurrentRecord(any());

    // when & then
    subject.deleteCurrentRecord(given);
    verify(accountingRecordsRepository).deleteRecord(accountingRecord);
  }

  @Test
  @DisplayName("deleteCurrentRecord - returns deleted record")
  void deleteCurrentRecord_shouldReturnDeletedRecord() {
    // given
    BotUser given = new BotUser();
    AccountingRecord accountingRecord = mock(AccountingRecord.class);

    doReturn(accountingRecord).when(accountingRecordsRepository).getCurrentRecord(any());

    // when & then
    assertEquals(accountingRecord, subject.deleteCurrentRecord(given));
  }

  @Test
  @DisplayName("finishCurrentRecord - gets current record from repo")
  void finishCurrentRecord_shouldGetCurrentRecordFromRepository() {
    BotUser given = new BotUser();
    doReturn(new AccountingRecord()).when(accountingRecordsRepository).getCurrentRecord(any());
    subject.finishCurrentRecord(given);
    verify(accountingRecordsRepository).getCurrentRecord(given);
  }

  @Test
  @DisplayName("finishCurrentRecord - closes fields of current record")
  void finishCurrentRecord_shouldCloseFieldsOfCurrentRecord() {
    // given
    BotUser given = new BotUser();
    AccountingRecord accountingRecord = mock(AccountingRecord.class);

    doReturn(accountingRecord).when(accountingRecordsRepository).getCurrentRecord(any());

    // when & then
    subject.finishCurrentRecord(given);
    verify(accountingRecord).closeEmptyFields();
  }

  @Test
  @DisplayName("finishCurrentRecord - saves closed record")
  void finishCurrentRecord_shouldSaveClosedRecord() {
    // given
    BotUser given = new BotUser();
    AccountingRecord accountingRecord = mock(AccountingRecord.class);
    AccountingRecord closedRecord = mock(AccountingRecord.class);

    doReturn(accountingRecord).when(accountingRecordsRepository).getCurrentRecord(any());
    doReturn(closedRecord).when(accountingRecord).closeEmptyFields();
    doReturn(closedRecord).when(accountingRecordsRepository).saveRecord(any());

    // when & then
    AccountingRecord actual = subject.finishCurrentRecord(given);
    verify(accountingRecordsRepository).saveRecord(closedRecord);
    assertEquals(closedRecord, actual);
  }



}