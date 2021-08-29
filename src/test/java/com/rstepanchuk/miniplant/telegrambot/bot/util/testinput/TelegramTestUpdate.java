package com.rstepanchuk.miniplant.telegrambot.bot.util.testinput;

import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramTestUpdate {

  public static Update getBasicUpdate() {
    Update update = new Update();
    update.setMessage(TelegramTestMessage.getBasicMessage());
    return update;
  }
}
