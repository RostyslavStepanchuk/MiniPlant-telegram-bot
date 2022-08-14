package com.rstepanchuk.miniplant.telegrambot.bot.util.testinput;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramTestUpdate {

  public static final String DEFAULT_CALLBACK_DATA = "Callback data";

  public static Update getBasicMessageUpdate() {
    Update update = new Update();
    update.setMessage(TelegramTestMessage.getBasicMessage());
    return update;
  }

  public static Update getBasicCallbackUpdate() {
    Update update = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    User basicUser = TelegramTestUser.getBasicUser();
    callbackQuery.setData(DEFAULT_CALLBACK_DATA);
    callbackQuery.setFrom(basicUser);
    update.setCallbackQuery(callbackQuery);
    return update;
  }
}
