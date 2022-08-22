package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.DEFAULT_SHEETS_PAGE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PROVIDE_SHEETS_PAGE_NAME;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupCleaner;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class DialogStageSheetIdSetup implements DialogStage, MarkupCleaner {

  private final ConfigurationService configurationService;

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {

    SheetsTableCredentials incoming = mapInputToCredentials(update.getMessage().getText());
    configurationService.updateTableCredentialsForUser(user, incoming);

    Message message = bot.execute(MessageBuilder
        .message(user.getId(), PROVIDE_SHEETS_PAGE_NAME)
        .withMarkup(MarkupBuilder.get()
            .addButton(DEFAULT_SHEETS_PAGE)
            .buildInline())
        .build());

    addMarkupToCleaningList(message, user);
    return Stages.SHEET_PAGE_SETUP;
  }

  @VisibleForTesting
  protected SheetsTableCredentials mapInputToCredentials(String input) {
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    credentials.setSheetId(input);
    return credentials;
  }

}
