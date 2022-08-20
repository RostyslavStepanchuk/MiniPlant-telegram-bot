package com.rstepanchuk.miniplant.telegrambot.bot.api;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

class MarkupBuilderTest {

  @Test
  @DisplayName("get - should return builder instance")
  void get_shouldReturnInstance() {
    assertThat(MarkupBuilder.get()).isInstanceOf(MarkupBuilder.class);
  }

  @Test
  @DisplayName("addInlineBtn - adds button to current row")
  void addInlineBtn_shouldAddInlineButtonToCurrentRow() {
    // given
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
    InlineKeyboardMarkup actual = MarkupBuilder.get()
        .addButton(button1)
        .addButton(button2)
        .buildInline();

    assertEquals(expectedMarkup, actual);
  }


  @Test
  @DisplayName("addButtonInNewRow - adds inline button to current row")
  void addButtonInNewRow_shouldAddButtonToCurrentRow() {
    // given
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
    InlineKeyboardMarkup actual = MarkupBuilder.get()
        .addButtonInNewRow(button1)
        .addButtonInNewRow(button2)
        .buildInline();

    assertEquals(expectedMarkup, actual);
  }

  @Test
  @DisplayName("can combine buttons to make rows and columns")
  void shouldBeAbleToCombineButtonsWithinRows() {
    // given
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
    InlineKeyboardMarkup actual = MarkupBuilder.get()
        .addButton(row1Btn1)
        .addButton(row1Btn2)
        .addButtonInNewRow(row2Btn1)
        .addButton(row2Btn2)
        .buildInline();

    assertEquals(expectedMarkup, actual);
  }

  @Test
  @DisplayName("hamburgerMenu - puts every button in new row")
  void hamburgerMenu_shouldPutEveryButtonToNewRow() {
    // given
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
    InlineKeyboardMarkup actual = MarkupBuilder.get()
        .hamburgerMenu(List.of(button1, button2))
        .buildInline();

    assertEquals(expectedMarkup, actual);
  }

}