package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
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
class DialogStageMainTest {

  private final DialogStageMain dialogStageMain = new DialogStageMain();

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  void execute() throws TelegramApiException {
    Update update = TelegramTestUpdate.getBasicUpdate();
    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    dialogStageMain.execute(update, bot);

    verify(bot).execute(messageCaptor.capture());
    SendMessage capturedMessage = messageCaptor.getValue();
    assertEquals("MAIN will be executed", capturedMessage.getText());
  }

  @Test
  void getNextStage_shouldPointToAccountingIncomeOrExpense() {
    assertEquals(ACCOUNTING_INC_EXP, dialogStageMain.getNextStage());
  }
}