package com.rstepanchuk.miniplant.telegrambot.bot.util.testinput;

import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramTestUser {

  public static final Long DEFAULT_USER_ID = 1L;

  public static User getBasicUser() {
    User user = new User();
    user.setId(DEFAULT_USER_ID);
    return user;
  }

}
