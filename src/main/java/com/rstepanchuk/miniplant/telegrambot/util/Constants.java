package com.rstepanchuk.miniplant.telegrambot.util;

public class Constants {

  private Constants() {
  }

  public static class Stages {

    private Stages() {
    }

    public static final String MAIN = "STAGE_MAIN";
    public static final String UNDEFINED = "STAGE_UNDEFINED";
    public static final String ACCOUNTING_INC_EXP = "STAGE_INCOME_EXPENSE";
  }

  //  public static class Buttons {
  //
  //    private Buttons() {
  //    }
  //
  //    public static final String INCOME = "Income";
  //    public static final String EXPENSES = "Expenses";
  //  }

  public static class Messages {

    private Messages() {
    }

    public static final String ONLY_PRIVATE_MESSAGES_ALLOWED = "Вибач, я спілкуюсь тільки"
        + " приватними повідомленнями. Я трохи старомодний в цих питаннях";
    public static final String BOT_IS_ONLY_FOR_SPECIFIC_USERS = "Я був створений тільки для"
        + " власниці Mini Plant. На жаль я не зможу тобі допомогти";
    public static final String MESSAGE_REQUIRES_TEXT = "Напиши мені щось, щоб я міг допомогти";
    public static final String STAGE_UNKNOWN = "Я заплутався, на чому ми зупинились. Давай "
        + "почнем з самого початку";
    public static final String STAGE_WILL_BE_RESET = "Давай почнем зпочатку";
    public static final String UNEXPECTED_ERROR = "Щось поламалось. Скажи своєму "
        + "горе-програмісту чоловіку, що мені погано";
    public static final String TELEGRAM_EXCEPTION = "Схоже, щось зламалось в Телеграмі. Розкажи "
        + "Ростику, йому буде цікаво";
  }
}
