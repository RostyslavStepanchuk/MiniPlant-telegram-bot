package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_WILL_BE_RESET;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DialogStageUndefined implements DialogStage<SendMessage> {

  @Override
  public Optional<SendMessage> execute(Update update) {
    return Optional.of(
        MessageBuilder.basicMessage(update, STAGE_WILL_BE_RESET)
        );
  }
}
