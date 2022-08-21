package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestMessage.DEFAULT_MESSAGE_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.CONFIGURATION_COMPLETE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.service.ConfigurationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageSheetPageSetupTest {

  @Spy
  @InjectMocks
  private DialogStageSheetPageSetup subject;

  @Mock
  private ConfigurationService configurationService;

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  @DisplayName("execute - clears previous markups")
  void execute_shouldClearPreviousMarkups() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).clearAllMarkups(givenUser, bot);
  }

  @Test
  @DisplayName("execute - maps input to credentials model")
  void execute_shouldMapInputToCredentialsModel() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doReturn(DEFAULT_MESSAGE_TEXT).when(subject).getData(any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).getData(givenUpdate);
    verify(subject).mapInputToCredentials(DEFAULT_MESSAGE_TEXT);
  }

  @Test
  @DisplayName("execute - updates credentials")
  void execute_shouldUpdateCredentials() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);
    SheetsTableCredentials credentials = mock(SheetsTableCredentials.class);

    doReturn(credentials).when(subject).mapInputToCredentials(any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(configurationService).updateTableCredentialsForUser(givenUser, credentials);
  }

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
    assertEquals(CONFIGURATION_COMPLETE, actual.getText());
  }

  @Test
  @DisplayName("execute - returns main stage")
  void execute_shouldReturnMainStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    // when & then
    assertEquals(Stages.MAIN, subject.execute(givenUpdate, bot, givenUser));
  }

  @Test
  @DisplayName("mapInputToCredentials - maps input to credentials")
  void mapInputToCredentials_shouldMapInputToCredentials() {
    String givenInput = "test";
    SheetsTableCredentials actual = subject.mapInputToCredentials(givenInput);
    assertEquals(givenInput, actual.getPageName());
  }
}