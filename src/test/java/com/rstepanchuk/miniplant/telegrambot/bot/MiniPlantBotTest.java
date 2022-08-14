package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.TELEGRAM_EXCEPTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class MiniPlantBotTest {

  private static final String EXCEPTION_TEST_MSG = "Test message";

  @Spy
  @InjectMocks
  private MiniPlantBot miniPlantBot;

  @Mock
  private UserFilter userFilter;

  @Mock
  private DialogStageHandler dialogStageHandler;

  @Test
  @DisplayName("processUpdate - handles valid updates")
  void processUpdate_handleValidUpdate() throws TelegramApiException {
    final BotUser user = new BotUser();
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    when(userFilter.authorizeUser(user.getId()))
        .thenReturn(user);

    miniPlantBot.processUpdate(user.getId(), update);

    verify(userFilter, times(1)).authorizeUser(user.getId());
    verify(dialogStageHandler, times(1)).handleStage(update, user, miniPlantBot);
  }

  @Test
  @DisplayName("processUpdate - should notify user if application exception")
  void processUpdate_whenMessageIsInvalid_shouldSendFailureMessageBack()
      throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    Long userId = 1L;
    doThrow(new ApplicationException(EXCEPTION_TEST_MSG))
        .when(userFilter).authorizeUser(any());

    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    miniPlantBot.processUpdate(userId, update);

    verify(miniPlantBot).execute(sendMessageCaptor.capture());

    SendMessage capturedMessage = sendMessageCaptor.getValue();
    assertEquals(EXCEPTION_TEST_MSG, capturedMessage.getText());
  }

  @Test
  @DisplayName("processUpdate - should notify user if unexpected exception")
  void processUpdate_whenExceptionOccur_shouldNotifyUser() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    long userId = 1L;
    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doThrow(new RuntimeException(EXCEPTION_TEST_MSG))
        .when(userFilter).authorizeUser(any());

    miniPlantBot.processUpdate(userId, update);

    verify(miniPlantBot).execute(sendMessageCaptor.capture());
    SendMessage capturedMessage = sendMessageCaptor.getValue();
    assertEquals(UNEXPECTED_ERROR, capturedMessage.getText());
  }

  @Test
  @DisplayName("processUpdate - should notify user if Telegram exception")
  void processUpdate_whenTelegramExceptionOccur_shouldNotifyUser() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    long userId = 1L;

    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doThrow(new TelegramApiException(EXCEPTION_TEST_MSG))
        .when(dialogStageHandler).handleStage(any(), any(), any());

    miniPlantBot.processUpdate(userId, update);

    verify(miniPlantBot).execute(sendMessageCaptor.capture());
    SendMessage capturedMessage = sendMessageCaptor.getValue();
    assertEquals(TELEGRAM_EXCEPTION, capturedMessage.getText());
  }

  @Test
  @DisplayName("onUpdateReceived - processes update if userId is present")
  void onUpdateReceived_shouldProcessUpdateIfUserIdPresent() {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    doReturn(Optional.of(DEFAULT_USER_ID)).when(userFilter).getUserId(any());
    doNothing().when(miniPlantBot).processUpdate(any(), any());

    // when & then
    miniPlantBot.onUpdateReceived(givenUpdate);
    verify(userFilter).getUserId(givenUpdate);
    verify(miniPlantBot).processUpdate(DEFAULT_USER_ID, givenUpdate);
  }

  @Test
  @DisplayName("onUpdateReceived - ignores update if userId is missing")
  void onUpdateReceived_shouldIgnoreUpdatesIfUnableToGerUserId() {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    doReturn(Optional.empty()).when(userFilter).getUserId(any());

    // when & then
    miniPlantBot.onUpdateReceived(givenUpdate);
    verify(miniPlantBot, times(0)).processUpdate(any(), any());
  }

  @Test
  void nameAndTokenGetters_shouldBePresent() {
    // Dummy test to include getters into code test coverage
    assertNull(miniPlantBot.getBotToken());
    assertNull(miniPlantBot.getBotUsername());
  }
}