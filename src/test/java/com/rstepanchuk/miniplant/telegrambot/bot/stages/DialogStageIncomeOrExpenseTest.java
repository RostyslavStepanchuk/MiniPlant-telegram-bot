package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsRepository;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageIncomeOrExpenseTest {

  @Spy
  @InjectMocks
  private DialogStageIncomeOrExpense subject;

  @Mock
  private AccountingService accountingService;

  @Mock
  private MenuOptionsRepository menuOptionsService;

  @Test
  @DisplayName("mapInputTextToAccountingRecord - sets input as type")
  void mapInputTextToAccountingRecord_shouldSetInputAsRecordType() {
    // given
    String input = "income";
    BotUser givenUser = new BotUser();

    // when & then
    AccountingRecord actual = subject.mapInputTextToAccountingRecord(input, givenUser);
    assertEquals(input, actual.getType());
  }

  @Test
  @DisplayName("mapInputTextToAccountingRecord - saves user to record")
  void mapInputTextToAccountingRecord_shouldSaveUserToRecord() {
    // given
    String input = "income";
    BotUser givenUser = mock(BotUser.class);

    // when & then
    AccountingRecord actual = subject.mapInputTextToAccountingRecord(input, givenUser);
    assertEquals(givenUser, actual.getUser());
  }

  @Test
  @DisplayName("sendSuccessNotification - sends notification to user")
  void sendSuccessNotification_shouldSendNotification() throws TelegramApiException {
    // given
    String nextStage = "next";
    String messageText = "notification message";
    List<String> menuOptions = List.of("option1", "option2");
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    AccountingRecord accountingRecord = mock(AccountingRecord.class);
    BotUser botUser = new BotUser();
    botUser.setId(DEFAULT_USER_ID);

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);
    doReturn(menuOptions).when(menuOptionsService).getOptionsByMenuName(any());
    doReturn(messageText).when(subject).getNextStageComingNotification(any());
    doNothing().when(subject).addMarkupToCleaningList(any(), any());
    doReturn(nextStage).when(subject).getNextStage();

    InlineKeyboardMarkup expectedMarkup = MarkupBuilder.get()
        .hamburgerMenu(menuOptions)
        .buildInline();

    // when
    subject.sendSuccessNotification(givenUpdate, bot, botUser, accountingRecord);

    // then
    verify(bot).execute(messageCaptor.capture());
    verify(subject).getNextStageComingNotification(accountingRecord);
    verify(menuOptionsService).getOptionsByMenuName(nextStage);
    SendMessage actualMessage = messageCaptor.getValue();
    assertEquals(String.valueOf(DEFAULT_USER_ID), actualMessage.getChatId());
    assertEquals(messageText, actualMessage.getText());
    assertEquals(expectedMarkup, actualMessage.getReplyMarkup());
  }

  @Test
  @DisplayName("sendSuccessNotification - adds message to markup clearance queue")
  void sendSuccessNotification_shouldAddMarkupForClearance() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    TelegramLongPollingBot bot = mock(TelegramLongPollingBot.class);
    AccountingRecord accountingRecord = mock(AccountingRecord.class);
    BotUser user = mock(BotUser.class);
    Message sentMessage = mock(Message.class);

    doReturn(sentMessage).when(bot).execute(any(SendMessage.class));

    // when & then
    subject.sendSuccessNotification(givenUpdate, bot, user, accountingRecord);
    verify(subject).addMarkupToCleaningList(sentMessage, user);
  }

  @Test
  @DisplayName("getNextStage - returns account selection stage")
  void getNextStage_shouldReturnAccountSelectionStage() {
    assertEquals(Stages.ACCOUNT_SELECTION, subject.getNextStage());
  }

  @Test
  @DisplayName("getNextStageComingNotification - returns valid message for income")
  void getNextStageComingNotification_shouldReturnIncomeRequestIfIncome() {
    AccountingRecord givenRecord = mock(AccountingRecord.class);
    doReturn(true).when(givenRecord).isIncome();
    doReturn("{record}").when(givenRecord).getType();

    String expected = "{record}\n" + Messages.SELECT_INCOME_ACCOUNT;

    assertEquals(expected,
        subject.getNextStageComingNotification(givenRecord));
  }

  @Test
  @DisplayName("getNextStageComingNotification - returns valid message for expense")
  void getNextStageComingNotification_shouldReturnExpenseRequestIfNotIncome() {
    AccountingRecord givenRecord = mock(AccountingRecord.class);
    doReturn(false).when(givenRecord).isIncome();
    doReturn("{record}").when(givenRecord).getType();

    String expected = "{record}\n" + Messages.SELECT_EXPENSE_ACCOUNT;

    assertEquals(expected,
        subject.getNextStageComingNotification(givenRecord));
  }
}