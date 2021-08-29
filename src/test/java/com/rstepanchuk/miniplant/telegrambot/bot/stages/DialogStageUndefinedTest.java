package com.rstepanchuk.miniplant.telegrambot.bot.stages;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.STAGE_WILL_BE_RESET;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

class DialogStageUndefinedTest {

  private DialogStageUndefined dialogStageUndefined = new DialogStageUndefined();

  @Test
  void execute_shouldReturnValidUserMessage() {
    Update update = TelegramTestUpdate.getBasicUpdate();
    Optional<SendMessage> result = dialogStageUndefined.execute(update);
    assertTrue(result.isPresent());
    assertEquals(STAGE_WILL_BE_RESET, result.get().getText());
  }

  @Test
  void getNextStage_shouldPointToMainStage() {
    assertEquals(MAIN, dialogStageUndefined.getNextStage());
  }
}