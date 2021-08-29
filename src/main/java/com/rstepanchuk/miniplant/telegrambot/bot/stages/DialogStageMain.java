package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DialogStageMain implements DialogStage {

  @Override
  public Optional<SendMessage> execute(Update update) {
    return Optional.of(
        MessageBuilder.basicMessage(update, "MAIN will be executed")
    );
  }

  @Override
  public String getNextStage() {
    return ACCOUNTING_INC_EXP;
  }
}
