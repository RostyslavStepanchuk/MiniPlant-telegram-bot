package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

class DialogStageIncomeOrExpenseTest {

  private DialogStageIncomeOrExpense dialogStageIncomeOrExpense
      = new DialogStageIncomeOrExpense();

  @Test
  void execute() {
    Update update = TelegramTestUpdate.getBasicUpdate();
    Optional<SendMessage> result = dialogStageIncomeOrExpense.execute(update);
    assertTrue(result.isPresent());
    assertEquals(result.get().getText(), "Expense choice will be executed");
  }
}