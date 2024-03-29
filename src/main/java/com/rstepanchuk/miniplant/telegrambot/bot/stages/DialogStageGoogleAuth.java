package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class DialogStageGoogleAuth implements DialogStage {

  private final GoogleCredentialsManager credentialsManager;

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {
    credentialsManager.authorize(bot, user);
    return Constants.Stages.MAIN;
  }
}
