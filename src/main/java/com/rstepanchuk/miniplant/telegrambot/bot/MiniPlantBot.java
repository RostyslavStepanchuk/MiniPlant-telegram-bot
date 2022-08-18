package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.TELEGRAM_EXCEPTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;

import java.io.Serializable;
import com.google.common.annotations.VisibleForTesting;
import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.exception.ApplicationException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RequiredArgsConstructor
@Slf4j
public class MiniPlantBot extends TelegramLongPollingBot {

  private final UserFilter userFilter;
  private final DialogStageHandler dialogStageHandler;

  @Value("${bot.name}")
  private String botUsername;

  @Value("${bot.token}")
  private String botToken;


  public String getBotUsername() {
    return botUsername;
  }

  public String getBotToken() {
    return botToken;
  }

  public void onUpdateReceived(Update update) {
    userFilter.getUserId(update)
        .ifPresent(id -> processUpdate(id, update));
  }

  @VisibleForTesting
  protected void processUpdate(Long userId, Update update) {
    try {
      BotUser user = userFilter.authorizeUser(userId);
      dialogStageHandler.handleStage(update, user, this);
    } catch (ApplicationException e) {
      log.error("Unexpected Application exception", e);
      exec(MessageBuilder.basicMessage(userId, e.getMessage()));
    } catch (TelegramApiException ex) {
      log.error("Unexpected Telegram exception", ex);
      exec(MessageBuilder.basicMessage(userId, TELEGRAM_EXCEPTION));
    } catch (Exception e) {
      log.error("Unexpected exception", e);
      exec(MessageBuilder.basicMessage(userId, UNEXPECTED_ERROR));
    }
  }

  private <T extends Serializable, M extends BotApiMethod<T>> void exec(M method) {
    try {
      execute(method);
    } catch (TelegramApiException e) {
      log.error("Unexpected Telegram exception", e);
    }
  }
}
