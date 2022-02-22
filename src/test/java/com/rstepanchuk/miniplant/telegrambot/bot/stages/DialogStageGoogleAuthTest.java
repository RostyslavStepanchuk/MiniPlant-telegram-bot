package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageGoogleAuthTest {

  @InjectMocks
  private DialogStageGoogleAuth subject;

  @Mock
  private GoogleCredentialsManager credentialsManager;

  @Mock
  private TelegramLongPollingBot bot;

  @DisplayName("execute - triggers credentialsManager authorization")
  @Test
  void execute_triggersAuthorization() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    subject.execute(update, bot);
    verify(credentialsManager).authorize(bot, update);
  }

  @DisplayName("execute - returns MAIN stage")
  @Test
  void execute_returnsMainStage() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    String actual = subject.execute(update, bot);
    assertEquals(Constants.Stages.MAIN, actual);
  }

}