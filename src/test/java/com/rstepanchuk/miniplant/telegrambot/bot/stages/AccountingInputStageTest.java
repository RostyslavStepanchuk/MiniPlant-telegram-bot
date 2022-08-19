package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.RECORD_DELETED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.RECORD_FINISHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class AccountingInputStageTest {

  AccountingInputStage subject;

  @Mock
  AccountingService accountingService;

  @Mock
  TelegramLongPollingBot bot;

  @BeforeEach
  void setupSubject() {
    subject = mock(
        AccountingInputStage.class,
        Mockito.withSettings()
            .useConstructor(accountingService));
  }

  @Test
  @DisplayName("execute - clears previous markups")
  void execute_shouldClearPreviousMarkups() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).clearAllMarkups(givenUser, bot);
  }

  @Test
  @DisplayName("execute - retrieves update text")
  void execute_shouldRetrieveUpdateText() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).getInput(givenUpdate);
  }

  @Test
  @DisplayName("execute - performs cancellation if canceled")
  void execute_shouldPerformCancellationIfCancelled() throws TelegramApiException {
    // given
    String cancelText = "cancel";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);
    doReturn(cancelText).when(subject).getInput(givenUpdate);
    doReturn(true).when(subject).isCancelled(cancelText);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).isCancelled(cancelText);
    verify(subject).performCancellation(givenUpdate, bot, givenUser);
  }

  @Test
  @DisplayName("execute - performs skipping if skipped")
  void execute_shouldPerformSkippingIfSkipped() throws TelegramApiException {
    // given
    String skipText = "skip";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);
    doReturn(skipText).when(subject).getInput(givenUpdate);
    doReturn(true).when(subject).isSkipped(skipText);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).isSkipped(skipText);
    verify(subject).performSkipping(givenUpdate, bot, givenUser);
  }

  @Test
  @DisplayName("execute - maps input to accounting record")
  void execute_shouldMapInputToAccountingRecord() throws TelegramApiException {
    // given
    String input = "someText";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);
    doReturn(input).when(subject).getInput(givenUpdate);

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).mapInputTextToAccountingRecord(input, givenUser);
  }

  @Test
  @DisplayName("execute - updates accounting record")
  void execute_shouldUpdateAccountingRecord() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    AccountingRecord incomingRecord = new AccountingRecord();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);
    doReturn(incomingRecord).when(subject).mapInputTextToAccountingRecord(any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(accountingService).updateAccountingRecord(incomingRecord);
  }

  @Test
  @DisplayName("execute - sends notification on success")
  void execute_shouldSendSuccessNotification() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    AccountingRecord updatedRecord = new AccountingRecord();

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);
    doReturn(updatedRecord).when(accountingService).updateAccountingRecord(any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).sendSuccessNotification(givenUpdate, bot, givenUser, updatedRecord);
  }

  @Test
  @DisplayName("execute - returns next stage")
  void execute_shouldReturnNextStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    String nextStage = "next";

    doCallRealMethod().when(subject).execute(givenUpdate, bot, givenUser);
    doReturn(nextStage).when(subject).getNextStage();

    // when & then
    String actual = subject.execute(givenUpdate, bot, givenUser);
    assertEquals(nextStage, actual);
    verify(subject).getNextStage();
  }

  @Test
  @DisplayName("performSkipping - finishes current record")
  void performSkipping_shouldStubCurrentRecord() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).performSkipping(givenUpdate, bot, givenUser);
    doReturn(mock(AccountingRecord.class)).when(accountingService).finishCurrentRecord(any());

    // when & then
    subject.performSkipping(givenUpdate, bot, givenUser);
    verify(accountingService).finishCurrentRecord(givenUser);
  }

  @Test
  @DisplayName("performSkipping - sends notification")
  void performSkipping_shouldSendNotification() throws TelegramApiException {
    // given
    String recordAsString = "{record}";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);
    AccountingRecord recordMock = mock(AccountingRecord.class);

    doCallRealMethod().when(subject).performSkipping(givenUpdate, bot, givenUser);
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doReturn(recordAsString).when(recordMock).inMessageFormat();
    doReturn(recordMock).when(accountingService).finishCurrentRecord(any());

    // when
    subject.performSkipping(givenUpdate, bot, givenUser);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage actual = messageCaptor.getValue();
    assertEquals(String.format(RECORD_FINISHED, recordAsString), actual.getText());
    assertEquals(String.valueOf(DEFAULT_USER_ID), actual.getChatId());
  }

  @Test
  @DisplayName("performSkipping - returns MAIN stage")
  void performSkipping_shouldReturnMainStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(mock(AccountingRecord.class)).when(accountingService).finishCurrentRecord(any());

    doCallRealMethod().when(subject).performSkipping(givenUpdate, bot, givenUser);

    // when & then
    assertEquals(Stages.MAIN, subject.performSkipping(givenUpdate, bot, givenUser));
  }

  @Test
  @DisplayName("performCancellation - deletes current record")
  void performCancellation_shouldDeleteCurrentRecord() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doCallRealMethod().when(subject).performCancellation(givenUpdate, bot, givenUser);
    doReturn(mock(AccountingRecord.class)).when(accountingService).deleteCurrentRecord(any());

    // when & then
    subject.performCancellation(givenUpdate, bot, givenUser);
    verify(accountingService).deleteCurrentRecord(givenUser);
  }

  @Test
  @DisplayName("performCancellation - sendsNotification")
  void performCancellation_shouldSendNotification() throws TelegramApiException {
    // given
    String recordAsString = "{record}";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);
    AccountingRecord recordMock = mock(AccountingRecord.class);

    doCallRealMethod().when(subject).performCancellation(givenUpdate, bot, givenUser);
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doReturn(recordAsString).when(recordMock).inMessageFormat();
    doReturn(recordMock).when(accountingService).deleteCurrentRecord(any());

    // when
    subject.performCancellation(givenUpdate, bot, givenUser);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage actual = messageCaptor.getValue();
    assertEquals(String.format(RECORD_DELETED, recordAsString), actual.getText());
    assertEquals(String.valueOf(DEFAULT_USER_ID), actual.getChatId());
  }

  @Test
  @DisplayName("performCancellation - returns MAIN stage")
  void performCancellation_shouldReturnMainStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(mock(AccountingRecord.class)).when(accountingService).deleteCurrentRecord(any());

    doCallRealMethod().when(subject).performCancellation(givenUpdate, bot, givenUser);

    // when & then
    assertEquals(Stages.MAIN, subject.performCancellation(givenUpdate, bot, givenUser));
  }

  @Test
  @DisplayName("getInput - getsInputFromUpdate")
  void performCancellation_shouldGetInputFromUpdate() {
    // given
    String expected = "input";
    Update given = TelegramTestUpdate.getBasicMessageUpdate();
    doReturn(expected).when(subject).getData(any());
    doCallRealMethod().when(subject).getInput(any());

    // when & then
    String actual = subject.getInput(given);
    assertEquals(expected, actual);
    verify(subject).getData(given);
  }
}