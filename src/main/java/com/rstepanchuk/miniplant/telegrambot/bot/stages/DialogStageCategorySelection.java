package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.RECORD_SAVED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageCategorySelection extends AccountingInputStage {

  public DialogStageCategorySelection(AccountingService accountingService) {
    super(accountingService);
  }

  @Override
  protected AccountingRecord mapInputTextToAccountingRecord(String input, BotUser user) {
    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setUser(user);
    accountingRecord.setCategory(input);
    return accountingRecord;
  }

  @Override
  protected void sendSuccessNotification(Update update,
                                         TelegramLongPollingBot bot,
                                         BotUser user,
                                         AccountingRecord currentRecord)
      throws TelegramApiException {
    bot.execute(MessageBuilder.basicMessage(user.getId(),
        RECORD_SAVED + currentRecord.inMessageFormat()));
  }

  @Override
  protected String getNextStage() {
    return Stages.MAIN;
  }
}
