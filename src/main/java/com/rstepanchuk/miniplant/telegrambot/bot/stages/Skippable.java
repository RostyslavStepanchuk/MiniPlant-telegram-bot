package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.SKIPPED_GENERAL;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface Skippable {

  String SKIP_COMMAND = "!";

  default boolean isSkipped(String inputText) {
    return null != inputText && inputText.equals(SKIP_COMMAND);
  }

  default String performSkipping(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {
    bot.execute(MessageBuilder.basicMessage(user.getId(), SKIPPED_GENERAL));
    return Constants.Stages.MAIN;
  }
}
