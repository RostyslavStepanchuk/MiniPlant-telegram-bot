package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_WILL_BE_RESET;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.TYPE_SELECTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.UNDEFINED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleAuthenticationException;
import com.rstepanchuk.miniplant.telegrambot.exception.SheetsNotSetUpException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepositoryImpl;
import com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

  @Spy
  @InjectMocks
  private DialogStageHandler subject;

  @Mock
  private UserRepositoryImpl userRepository;

  @Mock
  private ApplicationContext context;

  @Mock
  private DialogStage stageMock;

  @Mock
  private DialogStageUndefined undefinedStage;

  @Mock
  private DialogStageGoogleAuth googleAuthStage;

  @Mock
  private DialogStageSheetsConfig sheetsConfigStage;

  @Mock
  private TelegramLongPollingBot bot;


  @Test
  @DisplayName("Should take current stage from app context")
  void shouldTakeCurrentStageFromApplicationContext() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(stageMock);

    subject.handleStage(update, user, bot);

    verify(context).getBean(MAIN, DialogStage.class);
    verify(stageMock).execute(update, bot, user);
  }

  @Test
  @DisplayName("Should execute default stage if stage bean not found")
  void shouldReturnDefaultStage() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(any(), eq(DialogStage.class)))
        .thenThrow(new BeansException(""){});

    subject.handleStage(update, user, bot);

    verify(undefinedStage).execute(update, bot, user);
  }

  @Test
  @DisplayName("Should update user stage")
  void shouldUpdateUserStage() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(stageMock);
    doReturn(TYPE_SELECTION).when(stageMock).execute(any(), any(), eq(user));

    subject.handleStage(update, user, bot);

    assertEquals(TYPE_SELECTION, user.getStageId());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should notify user if application error")
  void shouldNotifyUserInCaseOfError() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = getTestBotUser();
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(stageMock);
    doThrow(new ApplicationException(EXCEPTION_TEST_MSG))
        .when(stageMock).execute(update, bot, user);

    subject.handleStage(update, user, bot);

    verify(bot, times(2)).execute(messageCaptor.capture());
    List<SendMessage> sentMessages = messageCaptor.getAllValues();
    assertEquals(2, sentMessages.size());
    assertEquals(EXCEPTION_TEST_MSG, sentMessages.get(0).getText());
    assertEquals(STAGE_WILL_BE_RESET, sentMessages.get(1).getText());
  }

  @Test
  @DisplayName("Should reset user stage to MAIN if application error")
  void shouldResetUserStageToMainInCaseOfError() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = getTestBotUser();
    user.setStageId(UNDEFINED);

    when(context.getBean(UNDEFINED, DialogStage.class)).thenReturn(stageMock);
    doThrow(new ApplicationException(EXCEPTION_TEST_MSG))
        .when(stageMock).execute(update, bot, user);

    subject.handleStage(update, user, bot);

    assertEquals(MAIN, user.getStageId());
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should notify if unexpected error")
  void shouldNotifyInCaseOfMajorError() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = getTestBotUser();

    when(context.getBean(MAIN, DialogStage.class)).thenReturn(stageMock);
    doThrow(new RuntimeException(EXCEPTION_TEST_MSG))
        .when(stageMock).execute(update, bot, user);
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    subject.handleStage(update, user, bot);

    verify(bot).execute(messageCaptor.capture());
    SendMessage sentMessage = messageCaptor.getValue();
    assertEquals(UNEXPECTED_ERROR, sentMessage.getText());
  }

  @Test
  @DisplayName("handleStage - sends message about authentication issue if any")
  void handleStage_whenGoogleAuthenticationException_shouldSendMessage()
      throws TelegramApiException {
    // given
    String exceptionMessage = "testMessage";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = getTestBotUser();

    GoogleAuthenticationException authException =
        new GoogleAuthenticationException(exceptionMessage);
    when(stageMock.execute(givenUpdate, bot, givenUser))
        .thenThrow(authException)
        .thenReturn(null);

    doReturn(stageMock).when(context).getBean(Stages.MAIN, DialogStage.class);

    // when
    subject.handleStage(givenUpdate, givenUser, bot);

    // then
    verify(bot).execute(MessageBuilder.basicMessage(givenUser.getId(), exceptionMessage));
  }

  @Test
  @DisplayName("handleStage - executes Google Auth stage if authentication exception")
  void handleStage_whenGoogleAuthenticationException_shouldExecuteGoogleAuthStage()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = getTestBotUser();

    when(stageMock.execute(givenUpdate, bot, givenUser))
        .thenThrow(new GoogleAuthenticationException("testMessage"))
        .thenReturn(null);

    doReturn(stageMock).when(context).getBean(Stages.MAIN, DialogStage.class);

    // when
    subject.handleStage(givenUpdate, givenUser, bot);

    // then
    verify(googleAuthStage).execute(givenUpdate, bot, givenUser);
  }

  @Test
  @DisplayName("handleStage - handles current stage again")
  void handleStage_whenGoogleAuthenticationException_shouldRepeatCurrentStage()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = getTestBotUser();

    when(stageMock.execute(givenUpdate, bot, givenUser))
        .thenThrow(new GoogleAuthenticationException("testMessage"))
        .thenReturn(null);

    doReturn(stageMock).when(context).getBean(Stages.MAIN, DialogStage.class);

    // when
    subject.handleStage(givenUpdate, givenUser, bot);

    // then
    verify(subject, times(2)).handleStage(givenUpdate, givenUser, bot);
  }

  @Test
  @DisplayName("handleStage - sends message is sheets credentials not set")
  void handleStage_whenSheetsNotSetUpException_shouldSendMessage()
      throws TelegramApiException {
    // given
    String exceptionMessage = "testMessage";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = getTestBotUser();

    SheetsNotSetUpException authException =
        new SheetsNotSetUpException(exceptionMessage);
    when(stageMock.execute(givenUpdate, bot, givenUser))
        .thenThrow(authException)
        .thenReturn(null);

    doReturn(stageMock).when(context).getBean(Stages.MAIN, DialogStage.class);

    // when
    subject.handleStage(givenUpdate, givenUser, bot);

    // then
    verify(bot).execute(MessageBuilder.basicMessage(givenUser.getId(), exceptionMessage));
  }

  @Test
  @DisplayName("handleStage - executes Sheets configuration if it's not set up")
  void handleStage_whenSheetsNotSetUpException_shouldExecuteSheetsConfigurationStage()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = getTestBotUser();
    String nextStage = "stageNextAfterConfig";

    when(stageMock.execute(givenUpdate, bot, givenUser))
        .thenThrow(new SheetsNotSetUpException("testMessage"));

    doReturn(stageMock).when(context).getBean(Stages.MAIN, DialogStage.class);
    doReturn(nextStage).when(sheetsConfigStage).execute(any(), any(), any());

    // when
    subject.handleStage(givenUpdate, givenUser, bot);

    // then
    verify(sheetsConfigStage).execute(givenUpdate, bot, givenUser);
    verify(givenUser).setStageId(nextStage);
  }

  private BotUser getTestBotUser() {
    BotUser user = spy(BotUser.class);
    user.setStageId(MAIN);
    user.setId(DEFAULT_USER_ID);
    return user;
  }
}