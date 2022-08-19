package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.NUMBER_INPUT_EXPECTED;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.PLEASE_SPECIFY_AMOUNT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.SELECT_TYPE;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsRepository;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageAmountInput extends AccountingInputStage {

  private final MenuOptionsRepository menuOptionsRepository;

  public DialogStageAmountInput(AccountingService accountingService,
                                MenuOptionsRepository menuOptionsRepository) {
    super(accountingService);
    this.menuOptionsRepository = menuOptionsRepository;
  }

  @Override
  public String execute(Update update, TelegramLongPollingBot bot, BotUser user)
      throws TelegramApiException {

    clearAllMarkups(user, bot);
    String stringInput = getInput(update);
    Optional<AccountingRecord> incomingRecord = mapAmountsInTextToRecord(stringInput, bot, user);

    if (incomingRecord.isPresent()) {
      AccountingRecord accountingRecord = accountingService
          .updateAccountingRecord(incomingRecord.get());

      sendSuccessNotification(update, bot, user, accountingRecord);

      return getNextStage();
    }

    return Stages.AMOUNT_INPUT;
  }

  @Override
  protected AccountingRecord mapInputTextToAccountingRecord(String input, BotUser user) {
    BigDecimal amount = BigDecimal
        .valueOf(Double.parseDouble(input))
        .setScale(2, RoundingMode.HALF_UP);

    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setAmount(amount);
    accountingRecord.setUser(user);
    return accountingRecord;
  }

  @Override
  protected void sendSuccessNotification(Update update,
                                         TelegramLongPollingBot bot,
                                         BotUser user,
                                         AccountingRecord currentRecord)
      throws TelegramApiException {

    String menuName = getNextStage();
    List<String> nextStageOptions = menuOptionsRepository.getOptionsByMenuName(menuName);
    Message messageWithMarkup = bot.execute(MessageBuilder
        .message(user.getId(), getSuccessNotificationText(currentRecord))
        .withMarkup(MarkupBuilder.get()
            .hamburgerMenu(nextStageOptions)
            .buildInline())
        .build());

    addMarkupToCleaningList(messageWithMarkup, user);
  }

  @Override
  protected String getNextStage() {
    return Stages.TYPE_SELECTION;
  }

  /**
   * For user convenience amount can be extracted form plain text. Regex is looking for all
   * numbers in message. If there is one number found, it's taken for execution, if multiple,
   * clarification message sent, if none, amount requested again
   *
   * @param data - text provided in update
   * @param bot    - mini plant bot
   * @return Optional of BigDecimal amount
   * @throws TelegramApiException telegram API exception
   */
  @VisibleForTesting
  protected Optional<AccountingRecord> mapAmountsInTextToRecord(String data,
                                                                TelegramLongPollingBot bot,
                                                                BotUser user
  ) throws TelegramApiException {
    try {
      List<String> numbersInMessage = extractNumbersFromText(data);
      if (numbersInMessage.size() == 1) {
        return Optional.of(mapInputTextToAccountingRecord(numbersInMessage.get(0), user));
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

  @VisibleForTesting
  protected String getSuccessNotificationText(AccountingRecord accountingRecord) {
    StringJoiner stringJoiner = new StringJoiner("\n");
    stringJoiner.add(accountingRecord.getAmount() + " грн")
        .add(SELECT_TYPE);
    return stringJoiner.toString();
  }

}
