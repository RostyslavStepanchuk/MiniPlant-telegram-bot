package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class DialogStageIncomeOrExpenseTest {

  private final DialogStageIncomeOrExpense dialogStageIncomeOrExpense
      = new DialogStageIncomeOrExpense();

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  void execute() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    dialogStageIncomeOrExpense.execute(update, bot);
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Expense choice will be executed", capturedMessage.getText());
  }
}