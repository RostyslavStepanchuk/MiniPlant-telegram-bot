package com.rstepanchuk.miniplant.telegrambot.service.accounting;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;

public interface AccountingService {

  AccountingRecord updateAccountingRecord(AccountingRecord accountingRecord);

  AccountingRecord deleteCurrentRecord(BotUser user);

  AccountingRecord finishCurrentRecord(BotUser user);

}
