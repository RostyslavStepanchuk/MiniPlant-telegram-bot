package com.rstepanchuk.miniplant.telegrambot.bot.util.testinput;

import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class TelegramTestChat {

  public static final Long DEFAULT_CHAT_ID = 1L;
  public static final String DEFAULT_CHAT_TYPE = "private";

  public static Chat getBasicChat() {
    return new ChatBuilder().build();
  }

  public static ChatBuilder builder() {
    return new ChatBuilder();
  }

  public static class ChatBuilder {

    private String type;
    private Long id;

    public ChatBuilder withType(String type) {
      this.type = type;
      return this;
    }

    public ChatBuilder withId(Long id) {
      this.id = id;
      return this;
    }

    public Chat build() {
      Chat chat = new Chat();
      chat.setId(Objects.requireNonNullElse(id, DEFAULT_CHAT_ID));
      chat.setType(Objects.requireNonNullElse(type, DEFAULT_CHAT_TYPE));
      return chat;
    }
  }
}
