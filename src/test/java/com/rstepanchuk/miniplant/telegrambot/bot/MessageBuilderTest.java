package com.rstepanchuk.miniplant.telegrambot.bot;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

class MessageBuilderTest {

  @Test
  void message_shouldReturnBuilder() {
    Update incomingUpdate = TelegramTestUpdate.getBasicUpdate();
    MessageBuilder builder = MessageBuilder
        .message(incomingUpdate, "Hi there");
    assertThat(builder).isInstanceOf(MessageBuilder.class);
  }

  @Test
  void message_shouldSetTextAndChatIdToMessage() {
    String messageText = "Hi there";
    Update incomingUpdate = TelegramTestUpdate.getBasicUpdate();
    SendMessage result = MessageBuilder
        .message(incomingUpdate, messageText).build();
    assertEquals(result.getText(), messageText, "SendMessage should have text provided as param");
    assertEquals(result.getChatId(),
        String.valueOf(incomingUpdate.getMessage().getChatId()),
        "Chat id should be copied from update");
  }

  @Test
  void basicMessage_shouldSetTextAndChatIdToMessage() {
    String messageText = "Hi there";
    Update incomingUpdate = TelegramTestUpdate.getBasicUpdate();
    SendMessage result = MessageBuilder
        .basicMessage(incomingUpdate, messageText);
    assertEquals(result.getText(), messageText, "SendMessage should have text provided as param");
    assertEquals(result.getChatId(),
        String.valueOf(incomingUpdate.getMessage().getChatId()),
        "Chat id should be copied from update");
  }

}