package com.rstepanchuk.miniplant.telegrambot.repository;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;

public interface AccountingRecordsRepository {

  AccountingRecord getCurrentRecord(BotUser user);

  AccountingRecord saveRecord(AccountingRecord accountingRecord);

  void deleteRecord(AccountingRecord accountingRecord);
}
