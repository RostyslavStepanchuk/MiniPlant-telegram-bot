package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageMain implements DialogStage {

  @Override
  public String execute(Update update, TelegramLongPollingBot bot) throws TelegramApiException {
    bot.execute(
        MessageBuilder.basicMessage(update, "MAIN will be executed")
    );
    return ACCOUNTING_INC_EXP;
  }
}
