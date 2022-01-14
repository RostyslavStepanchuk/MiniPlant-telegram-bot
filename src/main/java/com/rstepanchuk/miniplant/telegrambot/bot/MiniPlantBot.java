package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.TELEGRAM_EXCEPTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.UNEXPECTED_ERROR;

import java.io.Serializable;
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

  private final MessageValidator messageValidator;
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
    try {
      if (update.hasMessage()) {
        BotUser user = messageValidator.validateMessage(update.getMessage());
        dialogStageHandler.handleStage(update, user, this);
      }
    } catch (ApplicationException e) {
      exec(MessageBuilder.basicMessage(update, e.getMessage()));
    } catch (TelegramApiException ex) {
      log.error("Unexpected Telegram exception", ex);
      exec(MessageBuilder.basicMessage(update, TELEGRAM_EXCEPTION));
    } catch (Exception e) {
      exec(MessageBuilder.basicMessage(update, UNEXPECTED_ERROR));
    }
  }

  private <T extends Serializable, M extends BotApiMethod<T>> void exec(M method) {
    try {
      execute(method);
    } catch (TelegramApiException e) {
      log.error("Unexpected Telegram exception", e);
    }
  }

  //  private static ReplyKeyboardMarkup getSettingsKeyboard() {
  //    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
  //    replyKeyboardMarkup.setSelective(true);
  //    replyKeyboardMarkup.setResizeKeyboard(true);
  //    replyKeyboardMarkup.setOneTimeKeyboard(true);
  //
  //    List<KeyboardRow> keyboard = new ArrayList<>();
  //    KeyboardRow keyboardFirstRow = new KeyboardRow();
  //    keyboardFirstRow.add(INCOME);
  //    keyboardFirstRow.add(EXPENSES);
  //    keyboard.add(keyboardFirstRow);
  //    replyKeyboardMarkup.setKeyboard(keyboard);
  //
  //    return replyKeyboardMarkup;
  //  }
}
