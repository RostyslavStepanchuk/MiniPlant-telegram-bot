package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.EXPENSE_ACCOUNT_REQUEST;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.INCOME_ACCOUNT_REQUEST;

import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageIncomeOrExpense
    extends AccountingInputStage
    implements MessageOrCallbackAcceptable {

  public DialogStageIncomeOrExpense(AccountingService accountingService) {
    super(accountingService);
  }

  @Override
  protected String getInput(Update update) {
    return getData(update);
  }

  @Override
  protected AccountingRecord mapInputTextToAccountingRecord(String input, BotUser user) {
    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setUser(user);
    accountingRecord.setType(input);
    return accountingRecord;
  }

  @Override
  protected void sendSuccessNotification(Update update,
                                         TelegramLongPollingBot bot,
                                         AccountingRecord currentRecord)
      throws TelegramApiException {
    bot.execute(MessageBuilder
        .basicMessage(update.getMessage().getFrom().getId(),
            getNextStageComingNotification(currentRecord)));
  }

  @Override
  protected String getNextStage() {
    return Stages.ACCOUNT_SELECTION;
  }

  @VisibleForTesting
  protected String getNextStageComingNotification(AccountingRecord accountingRecord) {
    if (accountingRecord.isIncome()) {
      return INCOME_ACCOUNT_REQUEST;
    }
    return EXPENSE_ACCOUNT_REQUEST;
  }
}


