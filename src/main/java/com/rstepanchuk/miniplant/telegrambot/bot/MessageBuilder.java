package com.rstepanchuk.miniplant.telegrambot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageBuilder {

  private final Long chatId;
  private final String text;

  public static MessageBuilder message(Update update, String text) {
    return new MessageBuilder(update.getMessage().getChatId(), text);
  }

  public static SendMessage basicMessage(Update update, String text) {
    return message(update, text).build();
  }


  private MessageBuilder(Long chatId, String text) {
    this.chatId = chatId;
    this.text = text;
  }

  public SendMessage build() {
    return new SendMessage(String.valueOf(chatId), text);
  }

}
