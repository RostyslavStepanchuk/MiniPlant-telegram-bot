package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PROVIDE_SHEETS_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageSheetsConfigTest {

  private final DialogStageSheetsConfig subject = new DialogStageSheetsConfig();

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  @DisplayName("execute - notifies user about success")
  void execute_shouldNotifyUser() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(bot).execute(messageCaptor.capture());
    SendMessage actual = messageCaptor.getValue();
    assertEquals(String.valueOf(DEFAULT_USER_ID), actual.getChatId());
    assertEquals(PROVIDE_SHEETS_ID, actual.getText());
  }

  @Test
  @DisplayName("execute - returns main stage")
  void execute_shouldReturnMainStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    // when & then
    assertEquals(Stages.SHEET_ID_SETUP, subject.execute(givenUpdate, bot, givenUser));
  }
}