package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface DialogStage {
  /**
   * Method that encapsulates whole user dialog stage logic and returns name of the next stage.
   *
   * @param update telegram update object
   * @param bot telegram bot
   * @param user User model
   * @return next Stage name
   * @throws TelegramApiException exception thrown by Telegram library
   */
  String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException;

}
