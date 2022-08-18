package com.rstepanchuk.miniplant.telegrambot.bot.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkupBuilder {

  private final List<List<String>> buttons = new ArrayList<>();

  public static MarkupBuilder get() {
    return new MarkupBuilder();
  }

  public MarkupBuilder addButton(String text) {
    if (buttons.isEmpty()) {
      ArrayList<String> buttonsRow = new ArrayList<>();
      buttonsRow.add(text);
      this.buttons.add(buttonsRow);
    } else {
      this.buttons.get(this.buttons.size() - 1).add(text);
    }
    return this;
  }

  public MarkupBuilder addButtonInNewRow(String text) {
    ArrayList<String> buttonsRow = new ArrayList<>();
    buttonsRow.add(text);
    this.buttons.add(buttonsRow);
    return this;
  }

  public MarkupBuilder hamburgerMenu(List<String> buttons) {
    buttons.forEach(this::addButtonInNewRow);
    return this;
  }

  public InlineKeyboardMarkup buildInline() {
    InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> inlineButtons = buttons.stream()
        .map(row -> row.stream()
            .map(this::createInlineButton)
            .collect(Collectors.toList()))
        .collect(Collectors.toList());
    keyboard.setKeyboard(inlineButtons);
    return keyboard;
  }

  private InlineKeyboardButton createInlineButton(String text) {
    InlineKeyboardButton button = new InlineKeyboardButton(text);
    button.setCallbackData(text);
    return button;
  }

}
