package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface DialogStage {

  void executeStage(Update update);

  default String getNextStage() {
    return Constants.Stages.MAIN;
  }

  default String getPreviousStage() {
    return Constants.Stages.MAIN;
  }

}
