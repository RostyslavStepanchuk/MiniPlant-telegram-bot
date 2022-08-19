package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.RECORD_SAVED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageCategorySelectionTest {

  @InjectMocks
  private DialogStageCategorySelection subject;

  @Mock
  private AccountingService accountingService;

  @Test
  @DisplayName("mapInputTextToAccountingRecord - sets input as category")
  void mapInputTextToAccountingRecord_shouldSetInputAsCategory() {
    // given
    String input = "category";
    BotUser givenUser = mock(BotUser.class);

    // when & then
    AccountingRecord actual = subject.mapInputTextToAccountingRecord(input, givenUser);
    assertEquals(input, actual.getCategory());
    assertEquals(givenUser, actual.getUser());
  }

  @Test
  @DisplayName("sendSuccessNotification - sends notification to user")
  void sendSuccessNotification_shouldSendNotification() throws TelegramApiException {
    // given
    String recordAsString = "{formatted record}";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);
    AccountingRecord givenRecord = mock(AccountingRecord.class);
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    String expectedText = RECORD_SAVED + recordAsString;

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doReturn(recordAsString).when(givenRecord).inMessageFormat();

    // when
    subject.sendSuccessNotification(givenUpdate, bot, givenUser, givenRecord);

    // then
    verify(bot).execute(messageCaptor.capture());
    verify(givenRecord).inMessageFormat();

    SendMessage message = messageCaptor.getValue();
    assertEquals(String.valueOf(DEFAULT_USER_ID), message.getChatId());
    assertEquals(expectedText, message.getText());
  }

  @Test
  @DisplayName("getNextStage - returns MAIN stage")
  void getNextStage_shouldReturnsMainStage() {
    assertEquals(Stages.MAIN, subject.getNextStage());
  }
}