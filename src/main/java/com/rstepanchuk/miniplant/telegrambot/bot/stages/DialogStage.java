package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;

import java.util.Optional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface DialogStage<M extends BotApiMethod> {

  Optional<M> execute(Update update);

  default String getNextStage() {
    return MAIN;
  }

}
