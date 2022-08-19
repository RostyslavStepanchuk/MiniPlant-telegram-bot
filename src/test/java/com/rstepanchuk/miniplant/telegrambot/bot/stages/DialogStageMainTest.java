package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageMainTest {

  @Spy
  @InjectMocks
  private DialogStageMain subject;

  @Mock
  private DialogStageAmountInput dialogStageAmountInput;

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  @DisplayName("execute - clears previous markups")
  void execute_shouldClearPreviousMarkups() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);
    doNothing().when(subject).clearAllMarkups(any(), any());
    // when & then
    subject.execute(update, bot, givenUser);
    verify(subject).clearAllMarkups(givenUser, bot);
  }

  @Test
  @DisplayName("execute - extracts update data as String")
  void execute_shouldGetUpdateText() throws TelegramApiException {
    // given
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);
    doNothing().when(subject).clearAllMarkups(any(), any());
    // when & then
    subject.execute(update, bot, givenUser);
    verify(subject).getData(update);
  }

  @Test
  @DisplayName("execute - defines stage to be executed")
  void execute_shouldDefineStageForExecution() throws TelegramApiException {
    // given
    String data = "message text";
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    doNothing().when(subject).clearAllMarkups(any(), any());
    doReturn(data).when(subject).getData(any());

    // when & then
    subject.execute(update, bot, givenUser);
    verify(subject).startScenarioBasedOnInput(data);
  }

  @Test
  @DisplayName("execute - executes defined stage")
  void execute_shouldExecuteDefinedStage() throws TelegramApiException {
    // given
    String nextStage = "NEXT_STAGE";
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    doNothing().when(subject).clearAllMarkups(any(), any());
    doReturn(dialogStageAmountInput).when(subject).startScenarioBasedOnInput(any());
    doReturn(nextStage).when(dialogStageAmountInput).execute(any(), any(), any());

    // when & then
    String actual = subject.execute(update, bot, givenUser);
    verify(dialogStageAmountInput).execute(update, bot, givenUser);
    assertEquals(nextStage, actual);
  }
}