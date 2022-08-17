package com.rstepanchuk.miniplant.telegrambot.repository.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.AccountingRecordsGoogleSheets;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.AccountingRecordsJpa;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.AccountingRecordEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.AccountingRecordMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountingRecordsRepositoryImplTest {

  @Spy
  @InjectMocks
  private AccountingRecordsRepositoryImpl subject;

  @Mock
  private AccountingRecordsJpa jpa;

  @Mock
  private AccountingRecordsGoogleSheets sheets;

  @Mock
  private AccountingRecordMapper mapper;

  @Test
  @DisplayName("saveRecord - saves to sheets if record is complete")
  void saveRecord_shouldSaveToSheetsCompleteRecord() {
    // given
    AccountingRecord givenRecord = new AccountingRecord();
    doReturn(true).when(subject).recordIsComplete(any());
    // when & then
    subject.saveRecord(givenRecord);
    verify(sheets).save(givenRecord);
  }

  @Test
  @DisplayName("saveRecord - deletes record from memory if complete")
  void saveRecord_shouldDeleteFromMemoryCompleteRecord() {
    // given
    long givenRecordId = 1L;
    AccountingRecord givenRecord = new AccountingRecord();
    givenRecord.setId(givenRecordId);
    doReturn(true).when(subject).recordIsComplete(any());
    // when & then
    subject.saveRecord(givenRecord);
    verify(jpa).deleteById(givenRecordId);
    verifyNoMoreInteractions(jpa);
  }

  @Test
  @DisplayName("saveRecord - doesn't save to sheets incomplete record")
  void saveRecord_shouldNotSaveToJpaIncompleteRecord() {
    // given
    AccountingRecord givenRecord = new AccountingRecord();
    doReturn(false).when(subject).recordIsComplete(any());
    // when & then
    subject.saveRecord(givenRecord);
    verifyNoInteractions(sheets);
  }

  @Test
  @DisplayName("saveRecord - maps model to entity")
  void saveRecord_shouldMapAccountingRecordModelToEntity() {
    // given
    AccountingRecord givenRecord = new AccountingRecord();
    doReturn(false).when(subject).recordIsComplete(any());
    // when & then
    subject.saveRecord(givenRecord);
    verify(mapper).toAccountingRecordEntity(givenRecord);
  }

  @Test
  @DisplayName("saveRecord - saves to jpa incomplete record")
  void saveRecord_shouldSaveToJpaIncompleteRecord() {
    // given
    AccountingRecord givenRecord = new AccountingRecord();
    doReturn(false).when(subject).recordIsComplete(any());
    AccountingRecordEntity recordEntityMock = mock(AccountingRecordEntity.class);
    doReturn(recordEntityMock).when(mapper).toAccountingRecordEntity(givenRecord);
    // when
    AccountingRecord actual = subject.saveRecord(givenRecord);
    // then
    verify(jpa).save(recordEntityMock);
    assertEquals(givenRecord, actual);
  }

  @Test
  @DisplayName("getCurrentRecord - gets record from jpa")
  void getCurrentRecord_shouldGetCurrentMethodFromJpa() {
    // given
    long userId = 1L;
    BotUser given = new BotUser();
    given.setId(userId);

    // when
    subject.getCurrentRecord(given);

    // then
    verify(jpa).findByUserIdEquals(userId);
  }

  @Test
  @DisplayName("getCurrentRecord - maps record entity to model")
  void getCurrentRecord_shouldMapRecordEntityToModel() {
    // given
    BotUser given = new BotUser();
    AccountingRecordEntity recordEntity = new AccountingRecordEntity();
    AccountingRecord expected = new AccountingRecord();
    doReturn(Optional.of(recordEntity)).when(jpa).findByUserIdEquals(any());
    doReturn(expected).when(mapper).toAccountingRecord(any());

    // when
    AccountingRecord actual = subject.getCurrentRecord(given);

    // then
    verify(mapper).toAccountingRecord(recordEntity);
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("getCurrentRecord - creates new record model if missing")
  void getCurrentRecord_shouldCreateNewRecordModelIfMissing() {
    // given
    BotUser given = new BotUser();
    AccountingRecord expected = new AccountingRecord();
    doReturn(Optional.empty()).when(jpa).findByUserIdEquals(any());
    doReturn(expected).when(subject).createRecord(given);

    // when
    AccountingRecord actual = subject.getCurrentRecord(given);

    // then
    verify(subject).createRecord(given);
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("createRecord - creates new record with user")
  void createRecord_shouldCreateNewRecordWithUser() {
    BotUser given = new BotUser();
    AccountingRecord actual = subject.createRecord(given);
    assertEquals(given, actual.getUser());
  }

  @Test
  @DisplayName("recordIsComplete - true if key values non-null")
  void recordIsComplete_shouldBeTrueIfKeyValuesNonNull() {
    // given
    String notNullString = "qwerty";
    BigDecimal notNullAmount = BigDecimal.valueOf(1);
    AccountingRecord given = new AccountingRecord();
    given.setType(notNullString);
    given.setAmount(notNullAmount);
    given.setAccount(notNullString);
    given.setCategory(notNullString);

    // when & then
    assertTrue(subject.recordIsComplete(given));
  }

  @Test
  @DisplayName("recordIsComplete - false if amount is null")
  void recordIsComplete_shouldBeFalseIfAmountIsNull() {
    // given
    String notNullString = "qwerty";
    AccountingRecord given = new AccountingRecord();
    given.setType(notNullString);
    given.setAccount(notNullString);
    given.setCategory(notNullString);

    // when & then
    assertFalse(subject.recordIsComplete(given));
  }

  @Test
  @DisplayName("recordIsComplete - false if type is null")
  void recordIsComplete_shouldBeFalseIfTypeIsNull() {
    // given
    String notNullString = "qwerty";
    BigDecimal notNullAmount = BigDecimal.valueOf(1);
    AccountingRecord given = new AccountingRecord();
    given.setAmount(notNullAmount);
    given.setAccount(notNullString);
    given.setCategory(notNullString);

    // when & then
    assertFalse(subject.recordIsComplete(given));
  }

  @Test
  @DisplayName("recordIsComplete - false if account is null")
  void recordIsComplete_shouldBeFalseIfAccountIsNull() {
    // given
    String notNullString = "qwerty";
    BigDecimal notNullAmount = BigDecimal.valueOf(1);
    AccountingRecord given = new AccountingRecord();
    given.setType(notNullString);
    given.setAmount(notNullAmount);
    given.setCategory(notNullString);

    // when & then
    assertFalse(subject.recordIsComplete(given));
  }

  @Test
  @DisplayName("recordIsComplete - false if category is null")
  void recordIsComplete_shouldBeFalseIfCategoryIsNull() {
    // given
    String notNullString = "qwerty";
    BigDecimal notNullAmount = BigDecimal.valueOf(1);
    AccountingRecord given = new AccountingRecord();
    given.setType(notNullString);
    given.setAmount(notNullAmount);
    given.setAccount(notNullString);

    // when & then
    assertFalse(subject.recordIsComplete(given));
  }

  @Test
  @DisplayName("deleteRecord - deletes record")
  void deleteRecord_shouldDeleteRecord() {
    // given
    long recordId = 1L;
    AccountingRecord given = new AccountingRecord();
    given.setId(recordId);

    // when
    subject.deleteRecord(given);

    // then
    verify(jpa).deleteById(recordId);
  }

}