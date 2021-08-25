package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.MESSAGE_REQUIRES_TEXT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestMessage;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class MiniPlantBotTest {

  @Spy
  @InjectMocks
  private MiniPlantBot miniPlantBot;
  @Mock
  private MessageValidator messageValidator;

  private Update getBasicUpdate() {
    Update update = new Update();
    update.setMessage(TelegramTestMessage.getBasicMessage());
    return update;
  }

  @Test
  void onUpdateReceived_whenUpdateHasNoMessage_shouldNotStartValidation() throws TelegramApiException {
    Update update = new Update();
    miniPlantBot.onUpdateReceived(update);
    verify(miniPlantBot, never()).execute(any(SendMessage.class));
  }

  @Test
  void onUpdateReceived_whenMessageIsValid_shouldSendMessageBack() throws TelegramApiException {
    Update update = new Update();
    update.setMessage(TelegramTestMessage.getBasicMessage());
    when(messageValidator.validateMessage(update.getMessage()))
        .thenReturn(Optional.empty());
    doReturn(new Message()).when(miniPlantBot).execute(any(SendMessage.class));
    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    miniPlantBot.onUpdateReceived(update);
    verify(miniPlantBot).execute(sendMessageCaptor.capture());
    String text = sendMessageCaptor.getValue().getText();
    Long chatId = Long.valueOf(sendMessageCaptor.getValue().getChatId());

    assertEquals(update.getMessage().getText(), text);
    assertEquals(update.getMessage().getChatId(), chatId);
  }

  @Test
  void onUpdateReceived_whenMessageIsInvalid_shouldSendFailureMessageBack() throws TelegramApiException {
    Update update = new Update();
    update.setMessage(TelegramTestMessage.getBasicMessage());
    when(messageValidator.validateMessage(update.getMessage()))
        .thenReturn(Optional.of(MESSAGE_REQUIRES_TEXT));
    doReturn(new Message()).when(miniPlantBot).execute(any(SendMessage.class));
    ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    miniPlantBot.onUpdateReceived(update);
    verify(miniPlantBot).execute(sendMessageCaptor.capture());
    String text = sendMessageCaptor.getValue().getText();
    Long chatId = Long.valueOf(sendMessageCaptor.getValue().getChatId());

    assertEquals(MESSAGE_REQUIRES_TEXT, text);
    assertEquals(update.getMessage().getChatId(), chatId);
  }
}