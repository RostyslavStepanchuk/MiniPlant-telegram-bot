package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.EXPENSES;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.INCOME;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.AMOUNT_ACCEPTED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.NUMBER_INPUT_EXPECTED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PLEASE_SPECIFY_AMOUNT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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
class DialogStageMainTest {

  @Spy
  @InjectMocks
  private DialogStageMain subject;

  @Mock
  private AccountingService accountingService;

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
    verify(subject).getAmountInput(givenUpdate, bot, givenUser);
  }

  @Test
  @DisplayName("execute - returns Income/Expence stage if input present")
  void execute_shouldReturnIncomeExpenseIfAmountPresent() throws TelegramApiException {
    // given
    BigDecimal amount = BigDecimal.valueOf(1);
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(Optional.of(amount))
        .when(subject).getAmountInput(givenUpdate, bot, givenUser);
    doReturn(new Message()).when(subject).sendMessageForNextStage(any(), any(), any());

    // when & then
    String actual = subject.execute(givenUpdate, bot, givenUser);
    assertEquals(ACCOUNTING_INC_EXP, actual);
  }

  @Test
  @DisplayName("execute - returns main stage if input is missing")
  void execute_shouldReturnMainStageIfNoInput() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(Optional.empty())
        .when(subject).getAmountInput(givenUpdate, bot, givenUser);

    // when & then
    String actual = subject.execute(givenUpdate, bot, givenUser);
    assertEquals(MAIN, actual);
  }

  @Test
  @DisplayName("execute - updates record if amount is present")
  void execute_shouldUpdateRecordIfAmountPresent() throws TelegramApiException {
    // given
    BigDecimal amount = BigDecimal.valueOf(1);
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    AccountingRecord incomingRecord = new AccountingRecord();

    doReturn(Optional.of(amount))
        .when(subject).getAmountInput(givenUpdate, bot, givenUser);
    doReturn(incomingRecord)
        .when(subject).mapInputToAccountingRecord(amount, givenUser);
    doReturn(new Message()).when(subject).sendMessageForNextStage(any(), any(), any());

    // when
    subject.execute(givenUpdate, bot, givenUser);

    // then
    verify(subject).mapInputToAccountingRecord(amount, givenUser);
    verify(accountingService).updateAccountingRecord(incomingRecord);
  }

  @Test
  @DisplayName("execute - does nothing if amount is missing")
  void execute_shouldNotUpdateRecordIfAmountMissing() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();

    doReturn(Optional.empty())
        .when(subject).getAmountInput(givenUpdate, bot, givenUser);

    // when
    subject.execute(givenUpdate, bot, givenUser);

    // then
    verifyNoInteractions(accountingService);
    verify(subject, times(0))
        .sendMessageForNextStage(any(), any(), any());
    verifyNoInteractions(bot);
  }

  @Test
  @DisplayName("execute - sends message with inline buttons for next stage")
  void execute_shouldSendMessageWithInlineButtons() throws TelegramApiException {
    // given
    BigDecimal amount = BigDecimal.valueOf(1);
    long userId = 2L;
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    givenUser.setId(userId);
    AccountingRecord updatedRecord = new AccountingRecord();

    doReturn(Optional.of(amount))
        .when(subject).getAmountInput(givenUpdate, bot, givenUser);
    doReturn(updatedRecord)
        .when(accountingService).updateAccountingRecord(any());
    doReturn(new Message()).when(subject).sendMessageForNextStage(any(), any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).sendMessageForNextStage(updatedRecord, bot, userId);
  }

  @Test
  @DisplayName("execute - adds sent message to clearance queue")
  void execute_shouldSaveMessageForMarkupClearance() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = mock(BotUser.class);
    Message message = mock(Message.class);

    doReturn(Optional.of(BigDecimal.valueOf(1)))
        .when(subject).getAmountInput(givenUpdate, bot, givenUser);
    doReturn(message).when(subject).sendMessageForNextStage(any(), any(), any());

    // when & then
    subject.execute(givenUpdate, bot, givenUser);
    verify(subject).addMarkupToCleaningList(message, givenUser);
  }

  @Test
  @DisplayName("getAmountInput - gets string data from update")
  void getAmountInput_shouldGetStringDataFromUpdate() throws TelegramApiException {
    // given
    String amount = "1";
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(amount).when(subject).getData(givenUpdate);
    doReturn(Collections.emptyList()).when(subject).extractNumbersFromText(amount);

    // when & then
    subject.getAmountInput(givenUpdate, bot, givenUser);
    verify(subject).getData(givenUpdate);
    verify(subject).extractNumbersFromText(amount);
  }

  @Test
  @DisplayName("getAmountInput - returns optional big decimal amount with scale 2")
  void getAmountInput_shouldReturnAmountAsBigDecimal() throws TelegramApiException {
    // given
    String amount = "1";
    BigDecimal expected = BigDecimal.valueOf(1.00)
        .setScale(2, RoundingMode.HALF_UP);
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(List.of(amount)).when(subject).extractNumbersFromText(any());

    // when
    Optional<BigDecimal> actual = subject.getAmountInput(givenUpdate, bot, givenUser);

    // then
    assertTrue(actual.isPresent());
    assertEquals(expected, actual.get());
  }

  @Test
  @DisplayName("getAmountInput - returns empty optional if no number in input")
  void getAmountInput_shouldReturnEmptyOptionalIfNoNumbers() throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(Collections.emptyList())
        .when(subject).extractNumbersFromText(any());

    // when & then
    Optional<BigDecimal> actual = subject.getAmountInput(givenUpdate, bot, givenUser);
    assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("getAmountInput - returns empty optional if multiple numbers in input")
  void getAmountInput_shouldReturnEmptyOptionalIfMultipleNumbers()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(List.of("1", "2", "3"))
        .when(subject).extractNumbersFromText(any());
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when & then
    Optional<BigDecimal> actual = subject.getAmountInput(givenUpdate, bot, givenUser);
    assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("getAmountInput - returns empty optional if error with amount parsing")
  void getAmountInput_shouldReturnEmptyOptionalIfErrorDuringAmountParsing()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(List.of("1a"))
        .when(subject).extractNumbersFromText(any());

    // when & then
    Optional<BigDecimal> actual = subject.getAmountInput(givenUpdate, bot, givenUser);
    assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("getAmountInput - sends notification if no amount")
  void getAmountInput_shouldSendNotificationIfNoAmountOrParsingError()
      throws TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(Collections.emptyList())
        .when(subject).extractNumbersFromText(any());

    // when
    subject.getAmountInput(givenUpdate, bot, givenUser);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals(NUMBER_INPUT_EXPECTED, capturedMessage.getText());
  }

  @Test
  @DisplayName("getAmountInput - clarifies amount if multiple numbers")
  void getAmountInput_shouldClarifyAmountIfMultipleNumbers()
      throws TelegramApiException {
    // given
    List<String> givenAmounts = List.of("1", "2", "3");
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    BotUser givenUser = new BotUser();
    doReturn(givenAmounts)
        .when(subject).extractNumbersFromText(any());
    doNothing().when(subject).addMarkupToCleaningList(any(), any());

    // when & then
    subject.getAmountInput(givenUpdate, bot, givenUser);
    verify(subject).clarifyAmount(givenAmounts, givenUser, bot);
  }

  @Test
  @DisplayName("mapInputToAccountingRecord - creates record with user and amount")
  void mapInputToAccountingRecord_shouldClarifyAmount() {
    // given
    BigDecimal givenAmount = BigDecimal.valueOf(1.00);
    BotUser givenUser = new BotUser();

    // when
    AccountingRecord actual = subject.mapInputToAccountingRecord(givenAmount, givenUser);

    // then
    assertEquals(givenAmount, actual.getAmount());
    assertEquals(givenUser, actual.getUser());
  }

  @Test
  @DisplayName("sendMessageForNextStage - sends message with inline buttons")
  void sendMessageForNextStage_shouldSendMessageWithInlineButtons()
      throws TelegramApiException {
    // given
    BigDecimal givenAmount = BigDecimal.valueOf(1.00);
    Long chatId = 2L;
    AccountingRecord givenRecord = new AccountingRecord();
    givenRecord.setAmount(givenAmount);

    SendMessage expectedMessage = MessageBuilder
        .message(chatId, String.format(AMOUNT_ACCEPTED, givenAmount))
        .withMarkup(MarkupBuilder.get()
            .addButtonInNewRow(INCOME)
            .addButtonInNewRow(EXPENSES)
            .buildInline())
        .build();

    // when
    subject.sendMessageForNextStage(givenRecord, bot, chatId);

    // then
    verify(bot).execute(messageCaptor.capture());
    SendMessage actualMessage = messageCaptor.getValue();
    assertEquals(expectedMessage, actualMessage);
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
}