package com.rstepanchuk.miniplant.telegrambot.service.accounting;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {

  private final AccountingRecordsRepository recordsRepository;

  @Override
  public AccountingRecord updateAccountingRecord(AccountingRecord incomingRecord) {
    AccountingRecord updated = recordsRepository
        .getCurrentRecord(incomingRecord.getUser())
        .merge(incomingRecord);
    return recordsRepository.saveRecord(updated);
  }

  @Override
  public AccountingRecord deleteCurrentRecord(BotUser user) {
    AccountingRecord currentRecord = recordsRepository.getCurrentRecord(user);
    recordsRepository.deleteRecord(currentRecord);
    return currentRecord;
  }

  @Override
  public AccountingRecord finishCurrentRecord(BotUser user) {
    AccountingRecord currentRecord =
        recordsRepository.getCurrentRecord(user);
    return recordsRepository.saveRecord(currentRecord.closeEmptyFields());
  }

}
