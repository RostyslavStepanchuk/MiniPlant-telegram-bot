package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
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
class DialogStageUndefinedTest {

  private final DialogStageUndefined subject = new DialogStageUndefined();

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  void execute_shouldReturnValidUserMessage() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    BotUser user = new BotUser();

    // when
    subject.execute(update, bot, user);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals(STAGE_UNKNOWN, capturedMessage.getText());
  }

  @Test
  @DisplayName("execute - returns MAIN stage")
  void execute_returnsMainStage() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = new BotUser();
    String actual = subject.execute(update, bot, user);
    assertEquals(Constants.Stages.MAIN, actual);
  }

}