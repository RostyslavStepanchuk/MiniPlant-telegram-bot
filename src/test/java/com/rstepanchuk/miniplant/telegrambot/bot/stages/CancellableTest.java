package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CANCELLED_GENERAL;
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

class CancellableTest {

  private final Cancellable subject = new Cancellable(){};

  @Test
  @DisplayName("is cancelled - returns true if input equals cancel command")
  void isCancelled_shouldReturnTrueIfInputIsCancelCommand() {
    assertTrue(subject.isCancelled(Cancellable.CANCEL_COMMAND));
  }

  @Test
  @DisplayName("is cancelled - returns false if input is not a cancel command")
  void isCancelled_shouldReturnFalseIfInputIsCancelCommand() {
    assertFalse(subject.isCancelled("not_a_cancel_command"));
  }

  @Test
  @DisplayName("is cancelled - acceptsNull")
  void isCancelled_shouldAcceptNull() {
    assertFalse(subject.isCancelled(null));
  }

  @Test
  @DisplayName("performCancellation - notifies user")
  void performCancellation_shouldNotifyUser() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    // when
    subject.performCancellation(update, bot, givenUser);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage actual = messageCaptor.getValue();
    assertEquals(CANCELLED_GENERAL, actual.getText());
    assertEquals(String.valueOf(DEFAULT_USER_ID), actual.getChatId());
  }

  @Test
  @DisplayName("performCancellation - returns MAIN stage")
  void performCancellation_shouldReturnMainStage() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    BotUser givenUser = new BotUser();

    // when & then
    assertEquals(Constants.Stages.MAIN, subject.performCancellation(update, bot, givenUser));
  }
}