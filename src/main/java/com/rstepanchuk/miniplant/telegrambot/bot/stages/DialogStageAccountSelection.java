package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.SELECT_CATEGORY;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import java.util.List;
import java.util.StringJoiner;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MarkupBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.accounting.AccountingRecord;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsRepository;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class DialogStageAccountSelection extends AccountingInputStage {

  private final MenuOptionsRepository menuOptionsRepository;

  public DialogStageAccountSelection(AccountingService accountingService,
                                     MenuOptionsRepository menuOptionsRepository) {
    super(accountingService);
    this.menuOptionsRepository = menuOptionsRepository;
  }

  @Override
  protected AccountingRecord mapInputTextToAccountingRecord(String input, BotUser user) {
    AccountingRecord accountingRecord = new AccountingRecord();
    accountingRecord.setUser(user);
    accountingRecord.setAccount(input);
    return accountingRecord;
  }

  @Override
  protected void sendSuccessNotification(Update update,
                                         TelegramLongPollingBot bot,
                                         BotUser user,
                                         AccountingRecord currentRecord)
      throws TelegramApiException {

    String menuName = getNextStage() + "-" + currentRecord.getType();
    List<String> nextStageOptions = menuOptionsRepository.getOptionsByMenuName(menuName);
    Message messageWithMarkup = bot.execute(MessageBuilder
        .message(user.getId(),
            getNextStageComingNotification(currentRecord))
        .withMarkup(MarkupBuilder.get()
            .hamburgerMenu(nextStageOptions)
            .buildInline())
        .build());

    addMarkupToCleaningList(messageWithMarkup, user);
  }

  @Override
  protected String getNextStage() {
    return Stages.CATEGORY_SELECTION;
  }

  @VisibleForTesting
  protected String getNextStageComingNotification(AccountingRecord accountingRecord) {
    StringJoiner stringJoiner = new StringJoiner("\n");
    stringJoiner.add(accountingRecord.getAccount())
        .add(SELECT_CATEGORY);
    return stringJoiner.toString();
  }
}
