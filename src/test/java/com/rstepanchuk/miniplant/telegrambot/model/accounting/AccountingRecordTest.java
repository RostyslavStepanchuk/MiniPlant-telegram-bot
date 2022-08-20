package com.rstepanchuk.miniplant.telegrambot.model.accounting;

import static com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord.DEFAULT_VALUE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.INCOME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AccountingRecordTest {

  private final AccountingRecord subject = new AccountingRecord();

  @Test
  @DisplayName("merge - copies present values")
  void merge_shouldCopyNonNullValues() {
    // given
    String type = "type";
    String account = "account";
    String category = "category";
    BigDecimal amount = BigDecimal.valueOf(12.45);

    AccountingRecord incomingRecord = new AccountingRecord();
    incomingRecord.setType(type);
    incomingRecord.setAccount(account);
    incomingRecord.setCategory(category);
    incomingRecord.setAmount(amount);

    // when
    subject.merge(incomingRecord);

    // then
    assertEquals(type, subject.getType());
    assertEquals(account, subject.getAccount());
    assertEquals(category, subject.getCategory());
    assertEquals(amount, subject.getAmount());
  }

  @Test
  @DisplayName("merge - doesn't save incoming null values")
  void merge_shouldIgnoreIncomingNullValues() {
    // given
    String type = "type";
    String account = "account";
    String category = "category";
    BigDecimal amount = BigDecimal.valueOf(12.45);

    subject.setType(type);
    subject.setAccount(account);
    subject.setCategory(category);
    subject.setAmount(amount);

    AccountingRecord incoming = new AccountingRecord();

    // when
    subject.merge(incoming);

    // then
    assertEquals(type, subject.getType());
    assertEquals(account, subject.getAccount());
    assertEquals(category, subject.getCategory());
    assertEquals(amount, subject.getAmount());
  }

  @Test
  @DisplayName("merge - returns self")
  void merge_shouldReturnSelf() {
    AccountingRecord incomingRecord = mock(AccountingRecord.class);

    AccountingRecord actual = subject.merge(incomingRecord);
    assertEquals(subject, actual);
  }

  @Test
  @DisplayName("closeEmptyFields - sets default value to empty fields")
  void closeEmptyFields_shouldSetDefaultValueToEmptyFields() {
    // when
    subject.closeEmptyFields();

    // then
    assertEquals(DEFAULT_VALUE, subject.getType());
    assertEquals(DEFAULT_VALUE, subject.getAccount());
    assertEquals(DEFAULT_VALUE, subject.getCategory());
    assertNull(subject.getAmount());
  }

  @Test
  @DisplayName("closeEmptyFields - doesn't overwrite present values")
  void closeEmptyFields_shouldNotOverwritePresentValues() {
    // given
    String type = "type";
    String account = "account";
    String category = "category";
    BigDecimal amount = BigDecimal.valueOf(12.45);

    subject.setType(type);
    subject.setAccount(account);
    subject.setCategory(category);
    subject.setAmount(amount);

    // when
    subject.closeEmptyFields();

    // then
    assertEquals(type, subject.getType());
    assertEquals(account, subject.getAccount());
    assertEquals(category, subject.getCategory());
    assertEquals(amount, subject.getAmount());
  }

  @Test
  @DisplayName("closeEmptyFields - returns self")
  void closeEmptyFields_shouldReturnSelf() {
    assertEquals(subject, subject.closeEmptyFields());
  }

  @Test
  @DisplayName("inMessageFormat - represents model as message string")
  void inMessageFormat_shouldReturnModelInRightFormat() {
    // given
    String type = "type";
    String account = "account";
    String category = "category";
    BigDecimal amount = BigDecimal.valueOf(12.45);

    subject.setType(type);
    subject.setAccount(account);
    subject.setCategory(category);
    subject.setAmount(amount);

    String expected = "12.45 грн\n"
        + "Тип: type\n"
        + "Рахунок: account\n"
        + "Категорія: category";

    // when & then
    assertEquals(expected, subject.inMessageFormat());
  }

  @Test
  @DisplayName("inMessageFormat - ignores null fields")
  void inMessageFormat_shouldIgnoreEmptyFields() {
    // given
    String type = "type";
    BigDecimal amount = BigDecimal.valueOf(12.45);

    subject.setType(type);
    subject.setAmount(amount);

    String expected = "12.45 грн\n"
        + "Тип: type\n";

    // when & then
    assertEquals(expected, subject.inMessageFormat());
  }

  @Test
  @DisplayName("isIncome - returns true if type is income")
  void isIncome_shouldReturnTrueIfTypeIsIncome() {
    subject.setType(INCOME);
    assertTrue(subject.isIncome());
  }

  @Test
  @DisplayName("isIncome - returns false if type is not income")
  void isIncome_shouldReturnFalseIfTypeIsNotIncome() {
    subject.setType("not_income");
    assertFalse(subject.isIncome());
  }

  @Test
  @DisplayName("isIncome - throws NullPointer if not defined")
  void isIncome_shouldThrowNullPointerExceptionIfNotDefined() {
    assertThrows(NullPointerException.class, subject::isIncome);
  }
}