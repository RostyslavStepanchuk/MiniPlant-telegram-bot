package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageIncomeOrExpense implements DialogStage {

  @Override
  public void execute(Update update, TelegramLongPollingBot bot) throws TelegramApiException {
    bot.execute(MessageBuilder.basicMessage(update, "Expense choice will be executed"));
  }
}
