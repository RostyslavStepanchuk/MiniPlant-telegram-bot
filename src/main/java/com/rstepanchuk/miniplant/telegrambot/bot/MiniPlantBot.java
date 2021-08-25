package com.rstepanchuk.miniplant.telegrambot.bot;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MiniPlantBot extends TelegramLongPollingBot {

  private final MessageValidator messageValidator;

  @Value("${bot.name}")
  private String botUsername;

  @Value("${bot.token}")
  private String botToken;

  public MiniPlantBot(MessageValidator messageValidator) {
    this.messageValidator = messageValidator;
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
      SendMessage message = validationFailedMessage.map(s -> new SendMessage(
          String.valueOf(update.getMessage().getChatId()),
          s
      )).orElseGet(() -> new SendMessage(
          String.valueOf(update.getMessage().getChatId()),
          update.getMessage().getText()
      ));
      // Create a SendMessage object with mandatory fields
      //        message.enableMarkdown(true);
      //        message.setReplyMarkup(getSettingsKeyboard());
      try {
        execute(message); // Call method to send the message
      } catch (TelegramApiException e) {
        throw new IllegalStateException("Exceptions handling for Telegram API not implemented yet",
            e);
      }
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
