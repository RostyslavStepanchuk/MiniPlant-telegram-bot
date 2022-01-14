package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface DialogStage {

  void execute(Update update, TelegramLongPollingBot bot) throws TelegramApiException;

  default String getNextStage() {
    return MAIN;
  }

}
