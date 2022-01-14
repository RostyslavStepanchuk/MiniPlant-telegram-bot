package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_UNKNOWN;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
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

  private final DialogStageUndefined dialogStageUndefined = new DialogStageUndefined();

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  void execute_shouldReturnValidUserMessage() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicUpdate();
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    // when
    dialogStageUndefined.execute(update, bot);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals(STAGE_UNKNOWN, capturedMessage.getText());
  }

  @Test
  void getNextStage_shouldPointToMainStage() {
    assertEquals(MAIN, dialogStageUndefined.getNextStage());
  }
}