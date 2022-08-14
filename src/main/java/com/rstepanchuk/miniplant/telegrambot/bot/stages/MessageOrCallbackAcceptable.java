package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CANNOT_READ_UPDATE;

import com.rstepanchuk.miniplant.telegrambot.exception.ReadingUpdateException;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageOrCallbackAcceptable {

  default String getData(Update update) {
    if (update.hasMessage()) {
      return update.getMessage().getText();
    } else if (update.hasCallbackQuery()) {
      return update.getCallbackQuery().getData();
    }
    throw new ReadingUpdateException(CANNOT_READ_UPDATE);
  }

}
