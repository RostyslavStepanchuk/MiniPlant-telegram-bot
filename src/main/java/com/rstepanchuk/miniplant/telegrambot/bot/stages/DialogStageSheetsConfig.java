package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PROVIDE_SHEETS_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageSheetsConfig implements DialogStage {

  @Override
  public String execute(
      Update update,
      TelegramLongPollingBot bot,
      BotUser user) throws TelegramApiException {
    bot.execute(MessageBuilder.basicMessage(
        user.getId(),
        PROVIDE_SHEETS_ID
    ));
    return Stages.SHEET_ID_SETUP;
  }
}
