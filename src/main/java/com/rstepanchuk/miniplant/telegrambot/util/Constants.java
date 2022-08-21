package com.rstepanchuk.miniplant.telegrambot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  @UtilityClass
  public static class Stages {

    public static final String MAIN = "STAGE_MAIN";
    public static final String UNDEFINED = "STAGE_UNDEFINED";
    public static final String AMOUNT_INPUT = "STAGE_AMOUNT_INPUT";
    public static final String TYPE_SELECTION = "STAGE_INCOME_EXPENSE";
    public static final String ACCOUNT_SELECTION = "STAGE_ACCOUNT_SELECTION";
    public static final String CATEGORY_SELECTION = "STAGE_CATEGORY_SELECTION";
    public static final String GOOGLE_AUTH = "STAGE_GOOGLE_AUTH";
    public static final String CONFIGURATIONS = "STAGE_CONFIGURATIONS";
    public static final String SHEET_ID_SETUP = "STAGE_SHEET_ID_SETUP";
    public static final String SHEET_PAGE_SETUP = "STAGE_SHEET_PAGE_SETUP";
  }

  @UtilityClass
  public static class Buttons {

    public static final String INCOME = "приход";
    public static final String EXPENSES = "списание";
  }

  @UtilityClass
  public static class Messages {

    public static final String BOT_IS_ONLY_FOR_SPECIFIC_USERS = "Привіт! Я був створений тільки "
        + "для власниці Mini Plant і соромлюсь спілкуватись із незнайомими. Але можеш зазирнути "
        + "до нас в магазин:\n\nhttps://www.facebook.com/search/top?q=mini%20plant\n\n"
        + "https://www.instagram.com/mi.ni"
        + ".plant/?fbclid=IwAR2tfZ1gCN8PZhd9PIhyDs2LjqIcTvU36PdczIwIBtRlrCM5-KC570Sy89E\n\n"
        + "Впевнений, тобі будуть раді!";
    public static final String STAGE_UNKNOWN = "Я заплутався, на чому ми зупинились. Давай "
        + "почнем з самого початку";
    public static final String STAGE_WILL_BE_RESET = "Давай почнем зпочатку";
    public static final String UNEXPECTED_ERROR = "Щось поламалось. Скажи своєму "
        + "горе-програмісту чоловіку, що мені погано";
    public static final String TELEGRAM_EXCEPTION = "Схоже, щось зламалось в Телеграмі. Розкажи "
        + "Ростику, йому буде цікаво";
    public static final String GOOGLE_API_EXCEPTION = "Упс, я цього боявся - щось не так з "
        + "табличками Google.";
    public static final String GOOGLE_AUTH_EXCEPTION = "Мені не вдається зайти до твого акаунту "
        + "в Google, і я не можу з цим нічого зробити :(";
    public static final String AUTHENTICATION_REQUIRED = "Потрібно авторизуватись в "
        + "табличках";
    public static final String FOLLOW_AUTH_URL = "Будь ласка, пройди за "
        + "посиланням і передай Google, що я від тебе";
    public static final String CANT_SAVE_SHEETS_NOT_CONFIGURED = "Я не можу зберегти запис - не "
        + "знаю, в яку табличку записувати. Давай налаштуємо і спробуємо ще раз";
    public static final String CANT_APPEND_ROW = "Не виходить вставити рядок в Google Sheets. "
        + "Я не винний (хочеться вірити)";
    public static final String NUMBER_INPUT_EXPECTED = "Не зрозумів. Я очікував, що ти напишеш "
        + "мені суму, щоб внести її у звітність. Давай ще раз";
    public static final String PLEASE_SPECIFY_AMOUNT = "Вибач, я не зрозумів, що з цього сума, "
        + "яку потрібно записати?";
    public static final String CANNOT_READ_UPDATE = "Мені такого ще ніхто не присилав, не "
        + "знаю, як це прочитати";
    public static final String RECORD_FINISHED = "Записав, поки, що є. Не забудь потім "
        + "довнести:\n%s";
    public static final String RECORD_DELETED = "Ну нє - то нє. Видаляю запис:\n%s";
    public static final String CANCELLED_GENERAL = "Добре, відміняємо";
    public static final String SKIPPED_GENERAL = "Добре, пропускаємо";
    public static final String SELECT_TYPE = "Це дохід чи витрата?";
    public static final String SELECT_INCOME_ACCOUNT = "Як отримали?";
    public static final String SELECT_EXPENSE_ACCOUNT = "Як сплатили?";
    public static final String SELECT_CATEGORY = "В яку категорію записати?";
    public static final String RECORD_SAVED = "Записав в табличку:\n";
    public static final String PROVIDE_SHEETS_ID = "Відправ мені ID своєї google-таблички, "
        + "в яку ведуться записи. "
        + "Ти можеш знайти його, якщо зайдеш до таблиці, і подивишся у web-адресі:\n"
        + "'docs.google.com/spreadsheets/d/{ОСЬДО_ID}/edit'";
    public static final String PROVIDE_SHEETS_PAGE_NAME = "Ок, зберіг. Тепер напиши назву "
        + "сторінки, або вибери один з варіантів:";
    public static final String DEFAULT_SHEETS_PAGE = "♻️Журнал движения денег";
    public static final String CONFIGURATION_COMPLETE = "Готово. Тепер можна зберігати записи";


  }
}
