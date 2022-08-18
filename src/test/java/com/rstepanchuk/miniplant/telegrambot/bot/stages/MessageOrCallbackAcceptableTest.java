package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestMessage.DEFAULT_MESSAGE_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CANNOT_READ_UPDATE;
import static org.junit.jupiter.api.Assertions.*;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageOrCallbackAcceptable;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.ReadingUpdateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

class MessageOrCallbackAcceptableTest {

  MessageOrCallbackAcceptable subject = new MessageOrCallbackAcceptable() {};

  @Test
  @DisplayName("getData - gets text from Message if present")
  void getData_shouldGetTextFromMessage() {
    Update basicUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    assertEquals(DEFAULT_MESSAGE_TEXT, subject.getData(basicUpdate));
  }

  @Test
  @DisplayName("getData - gets data from Callback query if present")
  void getData_shouldGetDataFromCallbackQuery() {
    // given
    String callbackData = "callback";
    Update givenUpdate = new Update();
    CallbackQuery callbackQuery = new CallbackQuery();
    callbackQuery.setData(callbackData);
    givenUpdate.setCallbackQuery(callbackQuery);

    // when & then
    assertEquals(callbackData, subject.getData(givenUpdate));
  }

  @Test
  @DisplayName("getData - throws error if neither message, nor callback is present")
  void getData_shouldThrowErrorIfNeitherDataWasFound() {
    Update givenUpdate = new Update();
    ReadingUpdateException exception = assertThrows(
        ReadingUpdateException.class,
        () -> subject.getData(givenUpdate));
    assertEquals(CANNOT_READ_UPDATE, exception.getMessage());
  }
}