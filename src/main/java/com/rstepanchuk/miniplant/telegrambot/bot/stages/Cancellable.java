package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CANCELLED_GENERAL;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Cancellable {

  String CANCEL_COMMAND = "-";

  default boolean isCancelled(String inputText) {
    return null != inputText && inputText.equals(CANCEL_COMMAND);
  }

  default String performCancellation(
      Update update, TelegramLongPollingBot bot, BotUser user
  ) throws TelegramApiException {
    bot.execute(MessageBuilder.basicMessage(user.getId(), CANCELLED_GENERAL));
    return Constants.Stages.MAIN;
  }
}
