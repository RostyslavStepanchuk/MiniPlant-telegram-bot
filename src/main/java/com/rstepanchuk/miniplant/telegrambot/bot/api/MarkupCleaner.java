package com.rstepanchuk.miniplant.telegrambot.bot.api;

import java.util.ArrayList;
import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface MarkupCleaner {

  default void addMarkupToCleaningList(Message message, BotUser user) {
    List<Integer> savedMarkups = user.getMessagesWithMarkup();
    savedMarkups.add(message.getMessageId());
    user.setMessagesWithMarkup(savedMarkups);
  }

  default void clearAllMarkups(BotUser user, TelegramLongPollingBot bot)
      throws TelegramApiException {
    for (Integer integer : user.getMessagesWithMarkup()) {
      EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
      editMessageReplyMarkup.setChatId(String.valueOf(user.getId()));
      editMessageReplyMarkup.setMessageId(integer);
      bot.execute(editMessageReplyMarkup);
    }
    user.setMessagesWithMarkup(new ArrayList<>());
  }
}
