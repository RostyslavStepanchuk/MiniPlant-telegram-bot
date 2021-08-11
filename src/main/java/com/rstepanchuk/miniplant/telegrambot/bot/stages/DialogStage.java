package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class DialogStage {

  public abstract void executeStage(Update update);
  public String getNextStage() {
    return Constants.Stages.MAIN;
  };
  public String getPreviousStage() {
    return Constants.Stages.MAIN;
  }

}
