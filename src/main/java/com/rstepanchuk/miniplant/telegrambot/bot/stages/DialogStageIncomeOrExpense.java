package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.EXPENSE_ACCOUNT_REQUEST;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.INCOME_ACCOUNT_REQUEST;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import java.util.List;
import java.util.StringJoiner;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageOrCallbackAcceptable;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsService;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageIncomeOrExpense
    extends AccountingInputStage
    implements MessageOrCallbackAcceptable {

  private final MenuOptionsService menuOptionsService;

  public DialogStageIncomeOrExpense(
      AccountingService accountingService,
      MenuOptionsService menuOptionsService) {
    super(accountingService);
    this.menuOptionsService = menuOptionsService;
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
                                         BotUser user,
                                         AccountingRecord currentRecord)
      throws TelegramApiException {

    List<String> nextStageOptions = menuOptionsService.getOptionsForTheStage(getNextStage());
    Message messageWithMarkup = bot.execute(MessageBuilder
        .message(user.getId(),
            getNextStageComingNotification(currentRecord))
        .withMarkup(MarkupBuilder.get()
            .hamburgerMenu(nextStageOptions)
            .buildInline())
        .build());

    addMarkupToCleaningList(messageWithMarkup, user);
  }

  @Override
  protected String getNextStage() {
    return Stages.ACCOUNT_SELECTION;
  }

  @VisibleForTesting
  protected String getNextStageComingNotification(AccountingRecord accountingRecord) {
    StringJoiner stringJoiner = new StringJoiner("\n");
    stringJoiner.add(accountingRecord.inMessageFormat());
    if (accountingRecord.isIncome()) {
      stringJoiner.add(INCOME_ACCOUNT_REQUEST);
    } else {
      stringJoiner.add(EXPENSE_ACCOUNT_REQUEST);
    }
    return stringJoiner.toString();
  }
}


