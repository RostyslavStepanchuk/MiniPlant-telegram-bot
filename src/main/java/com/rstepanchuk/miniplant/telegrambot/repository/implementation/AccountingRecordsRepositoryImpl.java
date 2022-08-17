package com.rstepanchuk.miniplant.telegrambot.repository.implementation;

import java.util.Objects;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.AccountingRecordsGoogleSheets;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.AccountingRecordsJpa;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.AccountingRecordEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.AccountingRecordMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountingRecordsRepositoryImpl implements AccountingRecordsRepository {

  private final AccountingRecordsJpa jpa;
  private final AccountingRecordsGoogleSheets sheets;
  private final AccountingRecordMapper mapper;

  @Override
  public AccountingRecord saveRecord(AccountingRecord accountingRecord) {
    if (recordIsComplete(accountingRecord)) {
      jpa.deleteById(accountingRecord.getId());
      return sheets.save(accountingRecord);
    }
    AccountingRecordEntity accountingRecordEntity =
        mapper.toAccountingRecordEntity(accountingRecord);
    jpa.save(accountingRecordEntity);
    return accountingRecord;
  }

  @Override
  public void deleteRecord(AccountingRecord accountingRecord) {
    jpa.deleteById(accountingRecord.getId());
  }

  @Override
  public AccountingRecord getCurrentRecord(BotUser user) {
    return jpa.findByUserIdEquals(user.getId())
        .map(mapper::toAccountingRecord)
        .orElseGet(() -> createRecord(user));
  }

  @VisibleForTesting
  protected AccountingRecord createRecord(BotUser user) {
    AccountingRecord createdRecord = new AccountingRecord();
    createdRecord.setUser(user);
    return createdRecord;
  }

  @VisibleForTesting
  protected boolean recordIsComplete(AccountingRecord accountingRecord) {
    return Objects.nonNull(accountingRecord.getAmount())
        && Objects.nonNull(accountingRecord.getType())
        && Objects.nonNull(accountingRecord.getAccount())
        && Objects.nonNull(accountingRecord.getCategory());
  }

}
