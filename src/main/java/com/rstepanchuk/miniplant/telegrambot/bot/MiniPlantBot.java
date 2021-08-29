package com.rstepanchuk.miniplant.telegrambot.bot;

import java.io.Serializable;
import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MiniPlantBot extends TelegramLongPollingBot {

  private final MessageValidator messageValidator;
  private final DialogStageHandler dialogStageHandler;

  @Value("${bot.name}")
  private String botUsername;

  @Value("${bot.token}")
  private String botToken;

  public MiniPlantBot(MessageValidator messageValidator, DialogStageHandler dialogStageHandler) {
    this.messageValidator = messageValidator;
    this.dialogStageHandler = dialogStageHandler;
  }

  public String getBotUsername() {
    return botUsername;
  }

  public String getBotToken() {
    return botToken;
  }

  public void onUpdateReceived(Update update) {
    if (update.hasMessage()) {
      Optional<String> validationFailedMessage =
          messageValidator.validateMessage(update.getMessage());

      Optional<BotApiMethod<? extends Serializable>> output = validationFailedMessage.isPresent()
          ? validationFailedMessage.map(s ->
          MessageBuilder.basicMessage(update, s))
          : dialogStageHandler.handleStage(update);
      // Create a SendMessage object with mandatory fields
      //        message.enableMarkdown(true);
      //        message.setReplyMarkup(getSettingsKeyboard());
      output.ifPresent(o -> {
        try {
          execute(output.get());
        } catch (TelegramApiException e) {
          throw new IllegalStateException("Exceptions handling for Telegram API not "
              + "implemented yet",
              e);
        }
      });
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
