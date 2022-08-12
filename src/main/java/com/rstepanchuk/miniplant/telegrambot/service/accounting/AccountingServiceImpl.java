package com.rstepanchuk.miniplant.telegrambot.service.accounting;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {

  private final AccountingRecordsRepository recordsRepository;

  @Override
  public AccountingRecord updateAccountingRecord(AccountingRecord incomingRecord) {
    AccountingRecord currentRecord = recordsRepository.getCurrentRecord(incomingRecord.getUser());
    Optional.ofNullable(incomingRecord.getAmount()).ifPresent(currentRecord::setAmount);
    Optional.ofNullable(incomingRecord.getCategory()).ifPresent(currentRecord::setCategory);
    Optional.ofNullable(incomingRecord.getType()).ifPresent(currentRecord::setType);
    Optional.ofNullable(incomingRecord.getAccount()).ifPresent(currentRecord::setAccount);
    return recordsRepository.saveRecord(currentRecord);
  }

}
