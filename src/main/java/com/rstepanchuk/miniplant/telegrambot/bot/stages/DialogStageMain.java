package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.EXPENSES;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.INCOME;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.AMOUNT_ACCEPTED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.NUMBER_INPUT_EXPECTED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PLEASE_SPECIFY_AMOUNT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupCleaner;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageOrCallbackAcceptable;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
public class DialogStageMain implements DialogStage, MessageOrCallbackAcceptable, MarkupCleaner {

  private final AccountingService accountingService;

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {

    clearAllMarkups(user, bot);
    Optional<BigDecimal> amountInput = getAmountInput(update, bot, user);

    if (amountInput.isPresent()) {
      AccountingRecord accountingRecord = accountingService
          .updateAccountingRecord(mapInputToAccountingRecord(amountInput.get(), user));

      Message message = sendMessageForNextStage(accountingRecord, bot, user.getId());
      addMarkupToCleaningList(message, user);

      return ACCOUNTING_INC_EXP;
    }
    return MAIN;
  }

  /**
   * For user convenience amount can be extracted form plain text. Regex is looking for all
   * numbers in message. If there is one number found, it's taken for execution, if multiple,
   * clarification message sent, if none, amount requested again
   *
   * @param update - incoming update
   * @param bot    - mini plant bot
   * @return Optional of BigDecimal amount
   * @throws TelegramApiException telegram API exception
   */
  @VisibleForTesting
  protected Optional<BigDecimal> getAmountInput(Update update,
                                                TelegramLongPollingBot bot,
                                                BotUser user
  ) throws TelegramApiException {
    String data = getData(update);
    try {
      List<String> numbersInMessage = extractNumbersFromText(data);
      if (numbersInMessage.size() == 1) {
        return Optional.of(BigDecimal
            .valueOf(Double.parseDouble(numbersInMessage.get(0)))
            .setScale(2, RoundingMode.HALF_UP));
      } else if (numbersInMessage.isEmpty()) {
        bot.execute(MessageBuilder.basicMessage(user.getId(), NUMBER_INPUT_EXPECTED));
      } else {
        clarifyAmount(numbersInMessage, user, bot);
      }
    } catch (NumberFormatException e) {
      bot.execute(MessageBuilder.basicMessage(user.getId(), NUMBER_INPUT_EXPECTED));
    }
    return Optional.empty();
  }

  @VisibleForTesting
  protected AccountingRecord mapInputToAccountingRecord(BigDecimal amount, BotUser user) {
    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setAmount(amount);
    accountingRecord.setUser(user);
    return accountingRecord;
  }

  @VisibleForTesting
  protected Message sendMessageForNextStage(AccountingRecord accountingRecord,
                                         TelegramLongPollingBot bot,
                                         Long chatId) throws TelegramApiException {
    String acceptanceMessage = String.format(
        AMOUNT_ACCEPTED,
        accountingRecord.getAmount());
    return bot.execute(
        MessageBuilder.message(chatId, acceptanceMessage)
            .withMarkup(MarkupBuilder.get()
                .addButtonInNewRow(INCOME)
                .addButtonInNewRow(EXPENSES)
                .buildInline())
            .build());
  }

  @VisibleForTesting
  protected void clarifyAmount(List<String> numbersInMessage,
                               BotUser user,
                               TelegramLongPollingBot bot) throws TelegramApiException {
    SendMessage message = MessageBuilder
        .message(user.getId(), PLEASE_SPECIFY_AMOUNT)
        .withMarkup(MarkupBuilder.get()
            .hamburgerMenu(numbersInMessage)
            .buildInline())
        .build();

    addMarkupToCleaningList(bot.execute(message), user);
  }

  @VisibleForTesting
  protected List<String> extractNumbersFromText(String text) {
    String cleanText = text
        .replaceAll("\\s", "")
        .replaceAll("[-,:]|грн", ".");
    ArrayList<String> amounts = new ArrayList<>();
    Matcher matcher = Pattern.compile("\\d+\\.?(\\d+)?")
        .matcher(cleanText);
    while (matcher.find()) {
      amounts.add(matcher.group());
    }
    return amounts;
  }
}
