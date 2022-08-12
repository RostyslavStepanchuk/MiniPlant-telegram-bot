package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_UNKNOWN;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageUndefined implements DialogStage {

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {
    bot.execute(
        MessageBuilder.basicMessage(update, STAGE_UNKNOWN)
    );
    return Constants.Stages.MAIN;
  }
}
