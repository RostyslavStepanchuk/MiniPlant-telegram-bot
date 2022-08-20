package com.rstepanchuk.miniplant.telegrambot.bot.api;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class MarkupCleanerTest {

  @Spy
  private MarkupCleaner subject;

  @Mock
  private TelegramLongPollingBot bot;

  @Test
  @DisplayName("addMarkupToCleaningList - adds message ID to markups list")
  void addMarkupToCleaningList_shouldAddMessageIdToListOfUserMarkups() {
    // given
    int existingMarkupId = 1;
    int newMarkupId = 2;
    Message givenMessage = mock(Message.class);
    doReturn(newMarkupId).when(givenMessage).getMessageId();

    BotUser givenUser = new BotUser();
    givenUser.getMessagesWithMarkup().add(existingMarkupId);

    // when & then
    subject.addMarkupToCleaningList(givenMessage, givenUser);
    assertIterableEquals(List.of(existingMarkupId, newMarkupId),
        givenUser.getMessagesWithMarkup());
  }

  @Test
  @DisplayName("clearAllMarkups - executes edit for all markups")
  void clearAllMarkups_shouldExecuteMarkupEditForAllMarkups() throws TelegramApiException {
    // given
    int firstMessageId = 1;
    int secondMessageId = 2;

    BotUser givenUser = new BotUser();
    givenUser.setId(DEFAULT_USER_ID);
    givenUser.setMessagesWithMarkup(List.of(firstMessageId, secondMessageId));

    EditMessageReplyMarkup firstEdit = new EditMessageReplyMarkup();
    firstEdit.setChatId(String.valueOf(DEFAULT_USER_ID));
    firstEdit.setMessageId(firstMessageId);

    EditMessageReplyMarkup secondEdit = new EditMessageReplyMarkup();
    secondEdit.setChatId(String.valueOf(DEFAULT_USER_ID));
    secondEdit.setMessageId(secondMessageId);

    // when
    subject.clearAllMarkups(givenUser, bot);

    // then
    verify(bot).execute(firstEdit);
    verify(bot).execute(secondEdit);
  }

  @Test
  @DisplayName("clearAllMarkups - deletes saved markups of user")
  void clearAllMarkups_shouldDeleteSavedMarkups() throws TelegramApiException {
    // given
    int firstMessageId = 1;
    int secondMessageId = 2;

    BotUser givenUser = new BotUser();
    givenUser.setMessagesWithMarkup(List.of(firstMessageId, secondMessageId));

    // when & then
    subject.clearAllMarkups(givenUser, bot);
    assertTrue(givenUser.getMessagesWithMarkup().isEmpty());
  }
}