package com.rstepanchuk.miniplant.telegrambot.bot.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class MessageBuilder {

  private final Long chatId;
  private final String text;
  private ReplyKeyboard markup;

  public static MessageBuilder message(Long chatId, String text) {
    return new MessageBuilder(chatId, text);
  }

  public static SendMessage basicMessage(Long chatId, String text) {
    return message(chatId, text).build();
  }

  private MessageBuilder(Long chatId, String text) {
    this.chatId = chatId;
    this.text = text;
  }

  public MessageBuilder withMarkup(ReplyKeyboard markup) {
    this.markup = markup;
    return this;
  }

  public SendMessage build() {
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
    if (markup != null) {
      sendMessage.setReplyMarkup(markup);
    }
    return sendMessage;
  }

}
