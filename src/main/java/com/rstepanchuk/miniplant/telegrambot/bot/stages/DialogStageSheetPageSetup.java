package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CONFIGURATION_COMPLETE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupCleaner;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageOrCallbackAcceptable;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class DialogStageSheetPageSetup implements DialogStage,
    MarkupCleaner,
    MessageOrCallbackAcceptable {

  private final ConfigurationService configurationService;

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {

    clearAllMarkups(user, bot);
    SheetsTableCredentials credentials = mapInputToCredentials(getData(update));
    configurationService.updateTableCredentialsForUser(user, credentials);

    bot.execute(MessageBuilder.basicMessage(user.getId(), CONFIGURATION_COMPLETE));

    return Stages.MAIN;
  }

  @VisibleForTesting
  protected SheetsTableCredentials mapInputToCredentials(String input) {
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    credentials.setPageName(input);
    return credentials;
  }
}
