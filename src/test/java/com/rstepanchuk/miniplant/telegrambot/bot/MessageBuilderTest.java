package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

class MessageBuilderTest {

  @Test
  void message_shouldReturnBuilder() {
    MessageBuilder builder = MessageBuilder
        .message(DEFAULT_USER_ID, "Hi there");
    assertThat(builder).isInstanceOf(MessageBuilder.class);
  }

  @Test
  void message_shouldSetTextAndChatIdToMessage() {
    String messageText = "Hi there";
    SendMessage result = MessageBuilder
        .message(DEFAULT_USER_ID, messageText).build();
    assertEquals(result.getText(), messageText,
        "SendMessage should have text provided as param");
    assertEquals(result.getChatId(),
        String.valueOf(DEFAULT_USER_ID),
        "Chat id should equal provided ID");
  }

  @Test
  void basicMessage_shouldSetTextAndChatIdToMessage() {
    String messageText = "Hi there";
    SendMessage result = MessageBuilder
        .basicMessage(DEFAULT_USER_ID, messageText);
    assertEquals(result.getText(), messageText,
        "SendMessage should have text provided as param");
    assertEquals(result.getChatId(),
        String.valueOf(DEFAULT_USER_ID),
        "Chat id should equal provided ID");
  }

  @Test
  @DisplayName("withInlineBtn - adds inline button to current row")
  void withInlineBtn_shouldAddInlineButtonToCurrentRow() {
    // given
    String messageText = "Hi there";
    String button1 = "First Button";
    String button2 = "Second Button";

    // expected
    InlineKeyboardMarkup expectedMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton inlineButton1 = new InlineKeyboardButton(button1);
    inlineButton1.setCallbackData(button1);
    InlineKeyboardButton inlineButton2 = new InlineKeyboardButton(button2);
    inlineButton2.setCallbackData(button2);
    expectedMarkup.setKeyboard(List.of(List.of(inlineButton1, inlineButton2)));

    // when & then
    SendMessage actual = MessageBuilder.message(DEFAULT_USER_ID, messageText)
        .withInlineBtn(button1)
        .withInlineBtn(button2)
        .build();
    assertEquals(messageText, actual.getText());
    assertEquals(expectedMarkup, actual.getReplyMarkup());
  }

  @Test
  @DisplayName("withNewRowInlineBtn - adds inline button to current row")
  void withNewRowInlineBtn_shouldAddInlineButtonToCurrentRow() {
    // given
    String messageText = "Hi there";
    String button1 = "First Button";
    String button2 = "Second Button";

    // expected
    InlineKeyboardMarkup expectedMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton inlineButton1 = new InlineKeyboardButton(button1);
    inlineButton1.setCallbackData(button1);
    InlineKeyboardButton inlineButton2 = new InlineKeyboardButton(button2);
    inlineButton2.setCallbackData(button2);
    expectedMarkup.setKeyboard(
        List.of(
            List.of(inlineButton1),
            List.of(inlineButton2)
        ));

    // when & then
    SendMessage actual = MessageBuilder.message(DEFAULT_USER_ID, messageText)
        .withNewRowInlineBtn(button1)
        .withNewRowInlineBtn(button2)
        .build();
    assertEquals(messageText, actual.getText());
    assertEquals(expectedMarkup, actual.getReplyMarkup());
  }

  @Test
  @DisplayName("can combine inline buttons to make rows and columns")
  void shouldBeAbleToCombineButtonsWithinRows() {
    // given
    String messageText = "Hi there";
    String row1Btn1 = "First Row First Button";
    String row1Btn2 = "First Row Second Button";
    String row2Btn1 = "Second Row First Button";
    String row2Btn2 = "Second Row Second Button";

    // expected
    InlineKeyboardMarkup expectedMarkup = new InlineKeyboardMarkup();
    InlineKeyboardButton inlineButton1 = new InlineKeyboardButton(row1Btn1);
    inlineButton1.setCallbackData(row1Btn1);
    InlineKeyboardButton inlineButton2 = new InlineKeyboardButton(row1Btn2);
    inlineButton2.setCallbackData(row1Btn2);
    InlineKeyboardButton inlineButton3 = new InlineKeyboardButton(row2Btn1);
    inlineButton3.setCallbackData(row2Btn1);
    InlineKeyboardButton inlineButton4 = new InlineKeyboardButton(row2Btn2);
    inlineButton4.setCallbackData(row2Btn2);
    expectedMarkup.setKeyboard(
        List.of(
            List.of(inlineButton1, inlineButton2),
            List.of(inlineButton3, inlineButton4)
        ));

    // when & then
    SendMessage actual = MessageBuilder.message(DEFAULT_USER_ID, messageText)
        .withInlineBtn(row1Btn1)
        .withInlineBtn(row1Btn2)
        .withNewRowInlineBtn(row2Btn1)
        .withInlineBtn(row2Btn2)
        .build();
    assertEquals(messageText, actual.getText());
    assertEquals(expectedMarkup, actual.getReplyMarkup());
  }


}