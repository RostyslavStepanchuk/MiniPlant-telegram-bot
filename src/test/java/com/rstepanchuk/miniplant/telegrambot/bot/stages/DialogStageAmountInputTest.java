package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.NUMBER_INPUT_EXPECTED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PLEASE_SPECIFY_AMOUNT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.SELECT_TYPE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsRepository;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageAmountInputTest {

  @Spy
  @InjectMocks
  private DialogStageAmountInput subject;

  @Mock
  private AccountingService accountingService;

  @Mock
  private MenuOptionsRepository menuOptionsRepository;

  @Mock
  private TelegramLongPollingBot bot;

  private final ArgumentCaptor<SendMessage> messageCaptor =
      ArgumentCaptor.forClass(SendMessage.class);

  @Test
  @DisplayName("execute - clears markups")
  void execute_shouldClearMarkupsFromPreviousStage() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).clearAllMarkups(givenUser, bot);
  }

  @Test
  @DisplayName("execute - gets input from update")
  void execute_shouldGetInputFromUpdate() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).getData(givenUpdate);
  }

  @Test
  @DisplayName("execute - gets amount from update")
  void execute_shouldGetBidDecimalAmountFromUpdate() throws TelegramApiException {
    // given
    String amount = "15.50";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(amount).when(subject).getInput(givenUpdate);
    doNothing().when(subject).sendSuccessNotification(any(), any(), any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).mapAmountsInTextToRecord(amount, bot, givenUser);
  }

  @Test
  @DisplayName("execute - returns Type selection stage if input present")
  void execute_shouldReturnIncomeExpenseIfAmountPresent() throws TelegramApiException {
    // given
    AccountingRecord mappedRecord = mock(AccountingRecord.class);
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(Optional.of(mappedRecord))
        .when(subject).mapAmountsInTextToRecord(any(), any(), any());
    doNothing().when(subject).sendSuccessNotification(any(), any(), any(), any());

    // when & then
    String actual = subject.execute(givenUpdate, bot, givenUser);
    assertEquals(Stages.TYPE_SELECTION, actual);
  }

  @Test
  @DisplayName("execute - returns account input stage if input is missing")
  void execute_shouldReturnAmountInputStageIfNoInput() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(Optional.empty())
        .when(subject).mapAmountsInTextToRecord(any(), any(), any());

    // when & then
    String actual = subject.execute(givenUpdate, bot, givenUser);
    assertEquals(Stages.AMOUNT_INPUT, actual);
  }

  @Test
  @DisplayName("execute - maps input to accounting record")
  void execute_shouldMapInputToAccountingRecord() throws TelegramApiException {
    // given
    String input = "15.50";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);

    doReturn(input).when(subject).getInput(any());
    doNothing().when(subject).sendSuccessNotification(any(), any(), any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).mapAmountsInTextToRecord(input, bot, givenUser);
  }

  @Test
  @DisplayName("execute - updates record if amount is present")
  void execute_shouldUpdateRecordIfAmountPresent() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    AccountingRecord incomingRecord = mock(AccountingRecord.class);

    doReturn(Optional.of(incomingRecord))
        .when(subject).mapAmountsInTextToRecord(any(), any(), any());
    doNothing().when(subject).sendSuccessNotification(any(), any(), any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(accountingService).updateAccountingRecord(incomingRecord);
  }

  @Test
  @DisplayName("execute - does nothing if amount is missing")
  void execute_shouldNotUpdateRecordIfAmountMissing() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doReturn(Optional.empty())
        .when(subject).mapAmountsInTextToRecord(any(), any(), any());

    // when
    subject.execute(givenUpdate, bot, givenUser);

    // then
    verifyNoInteractions(accountingService);
    verify(subject, times(0))
        .sendSuccessNotification(any(), any(), any(), any());
  }

  @Test
  @DisplayName("sendSuccessNotification - gets options for next stage")
  void sendSuccessNotification_shouldGetOptionsForNextStage() throws TelegramApiException {
    // given
    String nextStage = "next";
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = mock(BotUser.class);
    AccountingRecord givenRecord = mock(AccountingRecord.class);

    doReturn(nextStage).when(subject).getNextStage();
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when
    subject.sendSuccessNotification(update, bot, user, givenRecord);

    // then
    verify(subject).getNextStage();
    verify(menuOptionsRepository).getOptionsByMenuName(nextStage);
  }

  @Test
  @DisplayName("sendSuccessNotification - sends message with inline buttons for next stage")
  void sendSuccessNotification_shouldSendMessageWithInlineButtons() throws TelegramApiException {
    // given
    List<String> options = List.of("option1", "option2");
    String successNotification = "hooray";
    Long userId = 1L;

    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = new BotUser();
    user.setId(userId);
    AccountingRecord givenRecord = mock(AccountingRecord.class);

    doNothing().when(subject).addMarkupToCleaningList(any(), any());
    doReturn(successNotification).when(subject).getSuccessNotificationText(any());
    doReturn(options).when(menuOptionsRepository).getOptionsByMenuName(any());


    SendMessage expected = MessageBuilder
        .message(userId, successNotification)
        .withMarkup(MarkupBuilder.get()
            .hamburgerMenu(options)
            .buildInline())
        .build();

    // when
    subject.sendSuccessNotification(update, bot, user, givenRecord);

    // then
    verify(subject).getSuccessNotificationText(givenRecord);
    verify(bot).execute(messageCaptor.capture());
    assertEquals(expected, messageCaptor.getValue());
  }

  @Test
  @DisplayName("sendSuccessNotification - adds sent message to clearance queue")
  void sendSuccessNotification_shouldSaveMessageForMarkupClearance() throws TelegramApiException {
    // given
    Message message = mock(Message.class);
    Update update = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser user = mock(BotUser.class);
    AccountingRecord givenRecord = mock(AccountingRecord.class);

    doNothing().when(subject).addMarkupToCleaningList(any(), any());
    doReturn(message).when(bot).execute(any(SendMessage.class));

    // when & then
    subject.sendSuccessNotification(update, bot, user, givenRecord);
    verify(subject).addMarkupToCleaningList(message, user);
  }


  @Test
  @DisplayName("mapInputTextToAccountingRecord - creates record with user and amount")
  void mapInputTextToAccountingRecord_shouldClarifyAmount() {
    // given
    String givenAmount = "15.00";
    BotUser givenUser = new BotUser();

    BigDecimal expectedAmount = BigDecimal
        .valueOf(15)
        .setScale(2, RoundingMode.HALF_UP);

    // when
    AccountingRecord actual = subject.mapInputTextToAccountingRecord(givenAmount, givenUser);

    // then
    assertEquals(expectedAmount, actual.getAmount());
    assertEquals(givenUser, actual.getUser());
  }

  @Test
  @DisplayName("getNextStage - returns type selection stage")
  void getNextStage_shouldClarifyAmount() {
    assertEquals(Stages.TYPE_SELECTION, subject.getNextStage());
  }


  @Test
  @DisplayName("mapAmountsInTextToRecord - extracts all numbers from text as strings")
  void mapAmountsInTextToRecord_shouldGetNumbersAsStringFromUpdate() throws TelegramApiException {
    // given
    String amount = "1";
    BotUser givenUser = new BotUser();

    doReturn(Collections.emptyList()).when(subject).extractNumbersFromText(any());

    // when & then
    subject.mapAmountsInTextToRecord(amount, bot, givenUser);
    verify(subject).extractNumbersFromText(amount);
  }

  @Test
  @DisplayName("mapAmountsInTextToRecord - returns accounting record if single number found")
  void mapAmountsInTextToRecord_shouldReturnAmountAsBigDecimal() throws TelegramApiException {
    // given
    String amount = "1";
    AccountingRecord expected = mock(AccountingRecord.class);
    BotUser givenUser = mock(BotUser.class);

    doReturn(List.of(amount)).when(subject).extractNumbersFromText(any());
    doReturn(expected)
        .when(subject).mapInputTextToAccountingRecord(any(), any());

    // when
    Optional<AccountingRecord> actual =
        subject.mapAmountsInTextToRecord(amount, bot, givenUser);

    // then
    verify(subject).mapInputTextToAccountingRecord(amount, givenUser);
    assertTrue(actual.isPresent());
    assertEquals(expected, actual.get());
  }

  @Test
  @DisplayName("mapAmountsInTextToRecord - returns empty optional if no number in input")
  void mapAmountsInTextToRecord_shouldReturnEmptyOptionalIfNoNumbers() throws TelegramApiException {
    // given
    BotUser givenUser = new BotUser();
    doReturn(Collections.emptyList())
        .when(subject).extractNumbersFromText(any());

    // when & then
    Optional<AccountingRecord> actual =
        subject.mapAmountsInTextToRecord("", bot, givenUser);
    assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("mapAmountsInTextToRecord - returns empty optional if multiple numbers in input")
  void mapAmountsInTextToRecord_shouldReturnEmptyOptionalIfMultipleNumbers()
      throws TelegramApiException {
    // given
    String amount = "1 2 3";
    BotUser givenUser = new BotUser();
    doReturn(List.of("1", "2", "3"))
        .when(subject).extractNumbersFromText(any());
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when & then
    Optional<AccountingRecord> actual =
        subject.mapAmountsInTextToRecord(amount, bot, givenUser);

    assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("mapAmountsInTextToRecord - returns empty optional if error with amount parsing")
  void mapAmountsInTextToRecord_shouldReturnEmptyOptionalIfErrorDuringAmountParsing()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(List.of("1a"))
        .when(subject).extractNumbersFromText(any());
    doThrow(NumberFormatException.class)
        .when(subject).mapInputTextToAccountingRecord(any(), any());

    // when & then
    Optional<AccountingRecord> actual =
        subject.mapAmountsInTextToRecord("", bot, givenUser);
    assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("mapAmountsInTextToRecord - sends notification if no amount")
  void mapAmountsInTextToRecord_shouldSendNotificationIfNoAmountOrParsingError()
      throws TelegramApiException {
    // given
    long userId = 1L;
    BotUser givenUser = new BotUser();
    givenUser.setId(userId);
    doReturn(Collections.emptyList())
        .when(subject).extractNumbersFromText(any());

    // when
    subject.mapAmountsInTextToRecord("", bot, givenUser);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals(NUMBER_INPUT_EXPECTED, capturedMessage.getText());
    assertEquals(String.valueOf(userId), capturedMessage.getChatId());
  }

  @Test
  @DisplayName("mapAmountsInTextToRecord - clarifies amount if multiple numbers")
  void mapAmountsInTextToRecord_shouldClarifyAmountIfMultipleNumbers()
      throws TelegramApiException {
    // given
    List<String> givenAmounts = List.of("1", "2", "3");
    BotUser givenUser = mock(BotUser.class);
    doReturn(givenAmounts)
        .when(subject).extractNumbersFromText(any());
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when & then
    subject.mapAmountsInTextToRecord("", bot, givenUser);
    verify(subject).clarifyAmount(givenAmounts, givenUser, bot);
  }

  @Test
  @DisplayName("clarifyAmount - sends message with amounts as buttons")
  void clarifyAmount_shouldSendMessageWithAmountsAsButtons()
      throws TelegramApiException {
    // given
    List<String> givenNumbers = List.of("1", "2", "3");
    long userId = 1L;
    BotUser givenUser = new BotUser();
    givenUser.setId(userId);
    SendMessage expectedMessage = MessageBuilder.message(userId, PLEASE_SPECIFY_AMOUNT)
        .withMarkup(MarkupBuilder.get()
            .hamburgerMenu(givenNumbers)
            .buildInline())
        .build();
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when
    subject.clarifyAmount(givenNumbers, givenUser, bot);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals(PLEASE_SPECIFY_AMOUNT, capturedMessage.getText());
    assertEquals(expectedMessage, capturedMessage);
  }

  @Test
  @DisplayName("clarifyAmount - adds message to markup clearance queue")
  void clarifyAmount_shouldAddMessageToMarkupClearance()
      throws TelegramApiException {
    // given
    List<String> givenNumbers = List.of("1", "2", "3");
    BotUser givenUser = mock(BotUser.class);
    Message expectedMessage = mock(Message.class);
    doNothing().when(subject).addMarkupToCleaningList(any(), any());
    doReturn(expectedMessage).when(bot).execute(any(SendMessage.class));

    // when
    subject.clarifyAmount(givenNumbers, givenUser, bot);

    // then
    verify(subject).addMarkupToCleaningList(expectedMessage, givenUser);
  }

  @ParameterizedTest
  @MethodSource("provideStringsForExtractNumbersFromText")
  @DisplayName("extractNumbersFromText - gets all numbers as string from text")
  void extractNumbersFromText_shouldGetAllNumbersAsStringsFromText(String input,
                                                                   List<String> expectedOutput) {
    assertIterableEquals(expectedOutput, subject.extractNumbersFromText(input));
  }

  private static Stream<Arguments> provideStringsForExtractNumbersFromText() {
    return Stream.of(
        Arguments.of("Вартість замовлення 25 грн 50 коп", List.of("25.50")),
        Arguments.of("75.25", List.of("75.25")),
        Arguments.of("75 грн", List.of("75.")),
        Arguments.of("75", List.of("75")),
        Arguments.of("75-25", List.of("75.25")),
        Arguments.of("75:25", List.of("75.25")),
        Arguments.of("Замовлення коштуватиме 542 грн, буде готове 25.03",
            List.of("542.", "25.03")),
        Arguments.of("Вартість 2 420 з доставкою, мій телефон (096)6675475",
            List.of("2420", "096", "6675475")),
        Arguments.of("повідомлення без чисел", Collections.emptyList())
    );
  }

  @Test
  @DisplayName("getSuccessNotificationText - returns valid message text")
  void getSuccessNotificationText_shouldAddMessageToMarkupClearance() {
    AccountingRecord given = new AccountingRecord();
    given.setAmount(BigDecimal.valueOf(15.00));
    String expected = "15.0 грн\n" + SELECT_TYPE;
    String actual = subject.getSuccessNotificationText(given);
    assertEquals(expected, actual);
  }
}