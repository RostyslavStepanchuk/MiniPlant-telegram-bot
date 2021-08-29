package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

class DialogStageMainTest {

  private DialogStageMain dialogStageMain = new DialogStageMain();

  @Test
  void execute() {
    Update update = TelegramTestUpdate.getBasicUpdate();
    Optional<SendMessage> result = dialogStageMain.execute(update);
    assertTrue(result.isPresent());
    assertEquals("MAIN will be executed", result.get().getText());
  }

  @Test
  void getNextStage_shouldPointToAccountingIncomeOrExpense() {
    assertEquals(ACCOUNTING_INC_EXP, dialogStageMain.getNextStage());
  }
}