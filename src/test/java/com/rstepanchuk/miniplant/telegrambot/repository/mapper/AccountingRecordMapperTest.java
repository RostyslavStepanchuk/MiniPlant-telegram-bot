package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.AccountingRecordEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountingRecordMapperTest {
  
  @InjectMocks
  AccountingRecordMapper subject = Mappers.getMapper(AccountingRecordMapper.class);

  @Mock
  UserMapper userMapper;
  
  @Test
  @DisplayName("toAccountingRecord - copies all values from entity")
  void toAccountingRecord_shouldCopyAllValues() {
    // when
    long recordId = 1L;
    String recordType = "type";
    BigDecimal recordAmount = BigDecimal.valueOf(17.25);
    String recordAccount = "account";
    String recordCategory = "category";
    String recordContractor = "contractor";
    String recordComment = "comment";
    BotUserEntity botUserEntity = new BotUserEntity();
    LocalDate recordEnteredDate = LocalDate.now();
    BotUser expectedUser = mock(BotUser.class);

    AccountingRecordEntity given = new AccountingRecordEntity();
    given.setId(recordId);
    given.setType(recordType);
    given.setAmount(recordAmount);
    given.setAccount(recordAccount);
    given.setCategory(recordCategory);
    given.setContractor(recordContractor);
    given.setComment(recordComment);
    given.setUser(botUserEntity);
    given.setEntered(recordEnteredDate);

    doReturn(expectedUser).when(userMapper).toBotUser(botUserEntity);
    
    // when
    AccountingRecord actual = subject.toAccountingRecord(given);
    // then
    assertEquals(recordId, actual.getId());
    assertEquals(recordType, actual.getType());
    assertEquals(recordAmount, actual.getAmount());
    assertEquals(recordAccount, actual.getAccount());
    assertEquals(recordCategory, actual.getCategory());
    assertEquals(recordContractor, actual.getContractor());
    assertEquals(recordComment, actual.getComment());
    assertEquals(expectedUser, actual.getUser());
    assertEquals(recordEnteredDate, actual.getEntered());
  }

  @Test
  @DisplayName("toAccountingRecordEntity - copies all values from model")
  void toAccountingRecordEntity() {
    // when
    long recordId = 1L;
    String recordType = "type";
    BigDecimal recordAmount = BigDecimal.valueOf(17.25);
    String recordAccount = "account";
    String recordCategory = "category";
    String recordContractor = "contractor";
    String recordComment = "comment";
    BotUser botUser = new BotUser();
    LocalDate recordEnteredDate = LocalDate.now();
    BotUserEntity expectedUser = mock(BotUserEntity.class);

    AccountingRecord given = new AccountingRecord();
    given.setId(recordId);
    given.setType(recordType);
    given.setAmount(recordAmount);
    given.setAccount(recordAccount);
    given.setCategory(recordCategory);
    given.setContractor(recordContractor);
    given.setComment(recordComment);
    given.setUser(botUser);
    given.setEntered(recordEnteredDate);

    doReturn(expectedUser).when(userMapper).toBotUserEntity(botUser);

    // when
    AccountingRecordEntity actual = subject.toAccountingRecordEntity(given);
    // then
    assertEquals(recordId, actual.getId());
    assertEquals(recordType, actual.getType());
    assertEquals(recordAmount, actual.getAmount());
    assertEquals(recordAccount, actual.getAccount());
    assertEquals(recordCategory, actual.getCategory());
    assertEquals(recordContractor, actual.getContractor());
    assertEquals(recordComment, actual.getComment());
    assertEquals(expectedUser, actual.getUser());
    assertEquals(recordEnteredDate, actual.getEntered());
  }

  @Test
  @DisplayName("toAccountingRecord - accepts null")
  void toAccountingRecord_acceptsNull() {
    assertNull(subject.toAccountingRecord(null));
  }

  @Test
  @DisplayName("toAccountingRecordEntity - accepts null")
  void toAccountingRecordEntity_acceptsNull() {
    assertNull(subject.toAccountingRecordEntity(null));
  }

}