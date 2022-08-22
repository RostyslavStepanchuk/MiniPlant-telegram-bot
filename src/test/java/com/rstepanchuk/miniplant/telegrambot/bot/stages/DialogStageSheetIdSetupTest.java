package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestMessage.DEFAULT_MESSAGE_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.DEFAULT_SHEETS_PAGE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PROVIDE_SHEETS_PAGE_NAME;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageSheetIdSetupTest {

  @Spy
  @InjectMocks
  private DialogStageSheetIdSetup subject;

  @Mock
  private ConfigurationService configurationService;

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  @DisplayName("execute - maps input to credentials model")
  void execute_shouldMapInputToCredentialsModel() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
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
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

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

    doNothing().when(subject).addMarkupToCleaningList(any(), any());
    SendMessage expected = MessageBuilder
        .message(DEFAULT_USER_ID, PROVIDE_SHEETS_PAGE_NAME)
        .withMarkup(MarkupBuilder.get()
            .addButton(DEFAULT_SHEETS_PAGE)
            .buildInline())
        .build();

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(bot).execute(messageCaptor.capture());
    SendMessage actual = messageCaptor.getValue();
    assertEquals(expected, actual);
  }

  @Test
  @DisplayName("execute - adds notification to markup clear list")
  void execute_shouldAddNotificationToClearanceList() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);
    Message message = mock(Message.class);

    doNothing().when(subject).addMarkupToCleaningList(any(), any());
    doReturn(message).when(bot).execute(any(SendMessage.class));

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).addMarkupToCleaningList(message, givenUser);
  }

  @Test
  @DisplayName("execute - returns page name setup page")
  void execute_shouldReturnSetupSheetNameStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when & then
    assertEquals(Stages.SHEET_PAGE_SETUP, subject.execute(givenUpdate, bot, givenUser));

  }

  @Test
  @DisplayName("mapInputToCredentials - maps input to credentials")
  void mapInputToCredentials_shouldMapInputToCredentials() {
    String givenInput = "test";
    SheetsTableCredentials actual = subject.mapInputToCredentials(givenInput);
    assertEquals(givenInput, actual.getSheetId());
  }
}