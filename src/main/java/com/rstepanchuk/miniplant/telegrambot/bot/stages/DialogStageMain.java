package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupCleaner;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageOrCallbackAcceptable;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class DialogStageMain implements
    DialogStage,
    MessageOrCallbackAcceptable,
    MarkupCleaner {

  @VisibleForTesting
  protected static final String  CONFIGURATION_COMMAND = "/setup";

  private final DialogStageAmountInput dialogStageAmountInput;
  private final DialogStageSheetsConfig dialogStageSheetsConfig;

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {

    clearAllMarkups(user, bot);
    return getFirstStageOfInputScenario(getData(update))
        .execute(update, bot, user);
  }

  @VisibleForTesting
  protected DialogStage getFirstStageOfInputScenario(String data) {
    if (CONFIGURATION_COMMAND.equals(data)) {
      return dialogStageSheetsConfig;
    }
    // currently, there is only one scenario - creating new accounting record
    // but in future there may be new scenarios, like Google Sheets configuration,
    // editing existing records which will be triggered by input in MAIN stage
    return dialogStageAmountInput;
  }

}
