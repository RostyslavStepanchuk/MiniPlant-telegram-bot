package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_WILL_BE_RESET;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.UNDEFINED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@ExtendWith(MockitoExtension.class)
class DialogStageHandlerTest {

  private static final String EXCEPTION_TEST_MSG = "Exception test message";

  private DialogStageHandler dialogStageHandler;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ApplicationContext context;

  @Mock
  private DialogStage defaultStage;

  @Mock
  private DialogStage currentStage;

  @Mock
  private TelegramLongPollingBot bot;

  @BeforeEach
  private void setup() {
    dialogStageHandler = new DialogStageHandler(userRepository, context, defaultStage);
  }

  @Test
  @DisplayName("Should take current stage from app context")
  void shouldTakeCurrentStageFromApplicationContext() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(currentStage);

    dialogStageHandler.handleStage(update, user, bot);

    verify(context).getBean(MAIN, DialogStage.class);
    verify(currentStage).execute(update, bot);
  }

  @Test
  @DisplayName("Should execute default stage if stage bean not found")
  void shouldReturnDefaultStage() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(any(), eq(DialogStage.class)))
        .thenThrow(new BeansException(""){});

    dialogStageHandler.handleStage(update, user, bot);

    verify(defaultStage).execute(update, bot);
  }

  @Test
  @DisplayName("Should update user stage")
  void shouldUpdateUserStage() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(currentStage);
    when(currentStage.getNextStage()).thenReturn(ACCOUNTING_INC_EXP);

    dialogStageHandler.handleStage(update, user, bot);

    assertEquals(ACCOUNTING_INC_EXP, user.getStageId());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should notify user if application error")
  void shouldNotifyUserInCaseOfError() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    BotUser user = getTestBotUser();
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(currentStage);
    doThrow(new ApplicationException(EXCEPTION_TEST_MSG))
        .when(currentStage).execute(update, bot);

    dialogStageHandler.handleStage(update, user, bot);

    verify(bot, times(2)).execute(messageCaptor.capture());
    List<SendMessage> sentMessages = messageCaptor.getAllValues();
    assertEquals(2, sentMessages.size());
    assertEquals(EXCEPTION_TEST_MSG, sentMessages.get(0).getText());
    assertEquals(STAGE_WILL_BE_RESET, sentMessages.get(1).getText());
  }

  @Test
  @DisplayName("Should reset user stage to MAIN if application error")
  void shouldResetUserStageToMainInCaseOfError() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    BotUser user = getTestBotUser();
    user.setStageId(UNDEFINED);

    when(context.getBean(UNDEFINED, DialogStage.class)).thenReturn(currentStage);
    doThrow(new ApplicationException(EXCEPTION_TEST_MSG))
        .when(currentStage).execute(update, bot);

    dialogStageHandler.handleStage(update, user, bot);

    assertEquals(MAIN, user.getStageId());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should notify if unexpected error")
  void shouldNotifyInCaseOfMajorError() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(currentStage);
    doThrow(new RuntimeException(EXCEPTION_TEST_MSG))
        .when(currentStage).execute(update, bot);
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    dialogStageHandler.handleStage(update, user, bot);

    verify(bot).execute(messageCaptor.capture());
    SendMessage sentMessage = messageCaptor.getValue();
    assertEquals(UNEXPECTED_ERROR, sentMessage.getText());
  }

  private BotUser getTestBotUser() {
    BotUser user = new BotUser();
    user.setStageId(MAIN);
    user.setId(DEFAULT_USER_ID);
    return user;
  }
}