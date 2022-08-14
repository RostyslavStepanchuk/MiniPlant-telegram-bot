package com.rstepanchuk.miniplant.telegrambot.bot.util.testinput;

import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class TelegramTestMessage {

  public static final String DEFAULT_MESSAGE_TEXT = "hello";

  public static Message getBasicMessage() {
    return new MessageBuilder().build();
  }

  public static Message getBasicMessage(String text) {
    return new MessageBuilder().withText(text).build();
  }

  public static MessageBuilder builder() {
    return new MessageBuilder();
  }

  public static class MessageBuilder {
    private Chat chat;
    private String text;
    private User from;

    public MessageBuilder withChat(Chat chat) {
      this.chat = chat;
      return this;
    }

    public MessageBuilder withText(String text) {
      this.text = text;
      return this;
    }

    public MessageBuilder from(User from) {
      this.from = from;
      return this;
    }

    public Message build() {
      Message message = new Message();
      message.setChat(Objects.requireNonNullElse(chat, TelegramTestChat.getBasicChat()));
      message.setFrom(Objects.requireNonNullElse(from, TelegramTestUser.getBasicUser()));
      message.setText(Objects.requireNonNullElse(text, DEFAULT_MESSAGE_TEXT));
      return message;
    }

  }
}
