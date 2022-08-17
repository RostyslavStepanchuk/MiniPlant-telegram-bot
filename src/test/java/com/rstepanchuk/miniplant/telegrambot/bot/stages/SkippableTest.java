package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.SKIPPED_GENERAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class SkippableTest {

  private final Skippable subject = new Skippable(){};

  @Test
  @DisplayName("isSkipped - returns true if input equals skip command")
  void isSkipped_shouldReturnTrueIfInputIsSkipCommand() {
    assertTrue(subject.isSkipped(Skippable.SKIP_COMMAND));
  }

  @Test
  @DisplayName("isSkipped - returns false if input is not a skip command")
  void isSkipped_shouldReturnFalseIfInputIsSkipCommand() {
    assertFalse(subject.isSkipped("not_a_Skip_command"));
  }

  @Test
  @DisplayName("is Skipped - acceptsNull")
  void isSkipped_shouldAcceptNull() {
    assertFalse(subject.isSkipped(null));
  }

  @Test
  @DisplayName("performSkipping - notifies user")
  void performSkipping_shouldNotifyUser() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    // when
    subject.performSkipping(update, bot, givenUser);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage actual = messageCaptor.getValue();
    assertEquals(SKIPPED_GENERAL, actual.getText());
    assertEquals(String.valueOf(DEFAULT_USER_ID), actual.getChatId());
  }

  @Test
  @DisplayName("performSkipping - returns MAIN stage")
  void performSkipping_shouldReturnMainStage() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    BotUser givenUser = new BotUser();

    // when & then
    assertEquals(Constants.Stages.MAIN, subject.performSkipping(update, bot, givenUser));
  }

}