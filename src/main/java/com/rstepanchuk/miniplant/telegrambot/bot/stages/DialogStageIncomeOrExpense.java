package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DialogStageIncomeOrExpense implements DialogStage {

  @Override
  public Optional<SendMessage> execute(Update update) {
    return Optional.of(MessageBuilder.basicMessage(update, "Expense choice will be executed"));
  }
}
