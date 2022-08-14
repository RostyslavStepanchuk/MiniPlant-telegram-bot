package com.rstepanchuk.miniplant.telegrambot.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Getter
public class MessageBuilder {

  private final Long chatId;
  private final String text;
  private final List<List<String>> inlineButtons = new ArrayList<>();

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

  public MessageBuilder withInlineBtn(String text) {
    if (inlineButtons.isEmpty()) {
      ArrayList<String> buttonsRow = new ArrayList<>();
      buttonsRow.add(text);
      this.inlineButtons.add(buttonsRow);
    } else {
      this.inlineButtons.get(this.inlineButtons.size() - 1).add(text);
    }
    return this;
  }

  public MessageBuilder withNewRowInlineBtn(String text) {
    ArrayList<String> buttonsRow = new ArrayList<>();
    buttonsRow.add(text);
    this.inlineButtons.add(buttonsRow);
    return this;
  }

  public SendMessage build() {
    SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
    if (!this.inlineButtons.isEmpty()) {
      addInlineMarkup(sendMessage);
    }
    return sendMessage;
  }

  private void addInlineMarkup(SendMessage message) {
    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> buttons = this.inlineButtons.stream()
        .map(row -> row.stream()
            .map(this::createInlineButton)
            .collect(Collectors.toList()))
        .collect(Collectors.toList());
    keyboard.setKeyboard(buttons);
    message.setReplyMarkup(keyboard);
  }

  private InlineKeyboardButton createInlineButton(String text) {
    InlineKeyboardButton button = new InlineKeyboardButton(text);
    button.setCallbackData(text);
    return button;
  }

}
