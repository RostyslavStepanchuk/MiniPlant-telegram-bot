package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.TELEGRAM_EXCEPTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.MessageValidationException;
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
  private MessageValidator messageValidator;

  @Mock
  private DialogStageHandler dialogStageHandler;

  @Test
  @DisplayName("Ignore updates without packages")
  void ignoreUpdatesWithoutMessage() throws TelegramApiException {
    Update update = new Update();

    miniPlantBot.onUpdateReceived(update);

    verify(messageValidator, never()).validateMessage(update.getMessage());
    verify(miniPlantBot, never()).execute(any(SendMessage.class));
    verify(dialogStageHandler, never()).handleStage(any(), any(), any());
  }

  @Test
  @DisplayName("Handle valid updates")
  void handleValidUpdate() throws TelegramApiException {
    final BotUser user = new BotUser();
    Update update = TelegramTestUpdate.getBasicUpdate();
    when(messageValidator.validateMessage(update.getMessage()))
        .thenReturn(user);

    miniPlantBot.onUpdateReceived(update);

    verify(messageValidator, times(1)).validateMessage(update.getMessage());
    verify(dialogStageHandler, times(1)).handleStage(update, user, miniPlantBot);
  }

  @Test
  @DisplayName("Should notify user if application exception")
  void onUpdateReceived_whenMessageIsInvalid_shouldSendFailureMessageBack() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();

    doThrow(new MessageValidationException(EXCEPTION_TEST_MSG))
        .when(messageValidator).validateMessage(update.getMessage());

    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    miniPlantBot.onUpdateReceived(update);

    verify(miniPlantBot).execute(sendMessageCaptor.capture());

    SendMessage capturedMessage = sendMessageCaptor.getValue();
    assertEquals(EXCEPTION_TEST_MSG, capturedMessage.getText());
  }

  @Test
  @DisplayName("Should notify user if unexpected exception")
  void onUpdateReceived_whenExceptionOccur_shouldNotifyUser() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doThrow(new RuntimeException(EXCEPTION_TEST_MSG))
        .when(messageValidator).validateMessage(update.getMessage());

    miniPlantBot.onUpdateReceived(update);

    verify(miniPlantBot).execute(sendMessageCaptor.capture());
    SendMessage capturedMessage = sendMessageCaptor.getValue();
    assertEquals(UNEXPECTED_ERROR, capturedMessage.getText());
  }

  @Test
  @DisplayName("Should notify user if Telegram exception")
  void onUpdateReceived_whenTelegramExceptionOccur_shouldNotifyUser() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();

    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doThrow(new TelegramApiException(EXCEPTION_TEST_MSG))
        .when(dialogStageHandler).handleStage(any(), any(), any());

    miniPlantBot.onUpdateReceived(update);

    verify(miniPlantBot).execute(sendMessageCaptor.capture());
    SendMessage capturedMessage = sendMessageCaptor.getValue();
    assertEquals(TELEGRAM_EXCEPTION, capturedMessage.getText());
  }

  @Test
  void nameAndTokenGetters_shouldBePresent() {
    // Dummy test to include getters into code test coverage
    assertNull(miniPlantBot.getBotToken());
    assertNull(miniPlantBot.getBotUsername());
  }
}