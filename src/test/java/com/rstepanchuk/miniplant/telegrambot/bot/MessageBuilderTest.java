package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.rstepanchuk.miniplant.telegrambot.bot.api.MessageBuilder;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

class MessageBuilderTest {

  private static final String DEFAULT_MESSAGE_TEXT = "Hi there";

  @Test
  void message_shouldReturnBuilder() {
    MessageBuilder builder = MessageBuilder
        .message(DEFAULT_USER_ID, DEFAULT_MESSAGE_TEXT);
    assertThat(builder).isInstanceOf(MessageBuilder.class);
  }

  @Test
  void message_shouldSetTextAndChatIdToMessage() {
    SendMessage result = MessageBuilder
        .message(DEFAULT_USER_ID, DEFAULT_MESSAGE_TEXT).build();
    assertEquals(DEFAULT_MESSAGE_TEXT, result.getText(),
        "SendMessage should have text provided as param");
    assertEquals(result.getChatId(),
        String.valueOf(DEFAULT_USER_ID),
        "Chat id should equal provided ID");
  }

  @Test
  void basicMessage_shouldSetTextAndChatIdToMessage() {
    SendMessage result = MessageBuilder
        .basicMessage(DEFAULT_USER_ID, DEFAULT_MESSAGE_TEXT);
    assertEquals(DEFAULT_MESSAGE_TEXT, result.getText(),
        "SendMessage should have text provided as param");
    assertEquals(result.getChatId(),
        String.valueOf(DEFAULT_USER_ID),
        "Chat id should equal provided ID");
  }

  @Test
  void withMarkup_shouldAddMarkupToMessage() {
    ReplyKeyboard markup = mock(ReplyKeyboard.class);

    SendMessage actual = MessageBuilder.message(DEFAULT_USER_ID, DEFAULT_MESSAGE_TEXT)
        .withMarkup(markup)
        .build();

    assertEquals(markup, actual.getReplyMarkup());
  }

}