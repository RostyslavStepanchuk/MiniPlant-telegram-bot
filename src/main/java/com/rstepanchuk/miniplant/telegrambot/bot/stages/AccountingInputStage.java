package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.RECORD_DELETED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.RECORD_FINISHED;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupCleaner;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageOrCallbackAcceptable;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public abstract class AccountingInputStage implements
    DialogStage,
    Cancellable,
    Skippable,
    MarkupCleaner,
    MessageOrCallbackAcceptable {

  protected final AccountingService accountingService;

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {
    clearAllMarkups(user, bot);
    String inputText = getInput(update);
    if (isCancelled(inputText)) {
      return performCancellation(update, bot, user);
    } else if (isSkipped(inputText)) {
      return performSkipping(update, bot, user);
    }
    AccountingRecord updatedRecord = accountingService
        .updateAccountingRecord(mapInputTextToAccountingRecord(inputText, user));

    sendSuccessNotification(update, bot, user, updatedRecord);

    return getNextStage();
  }

  @Override
  public String performSkipping(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {
    AccountingRecord finishedRecord = accountingService.finishCurrentRecord(user);
    bot.execute(
        MessageBuilder.basicMessage(user.getId(),
        String.format(RECORD_FINISHED, finishedRecord.inMessageFormat())));
    return Stages.MAIN;
  }

  @Override
  public String performCancellation(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {
    AccountingRecord deletedRecord = accountingService.deleteCurrentRecord(user);
    bot.execute(
        MessageBuilder.basicMessage(user.getId(),
            String.format(RECORD_DELETED, deletedRecord.inMessageFormat())));
    return Stages.MAIN;
  }

  protected String getInput(Update update) {
    return getData(update);
  }

  protected abstract AccountingRecord mapInputTextToAccountingRecord(String input, BotUser user);

  protected abstract void sendSuccessNotification(Update update,
                                                  TelegramLongPollingBot bot,
                                                  BotUser user,
                                                  AccountingRecord currentRecord)
      throws TelegramApiException;

  protected abstract String getNextStage();
}
