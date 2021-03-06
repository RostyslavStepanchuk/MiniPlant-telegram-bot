package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.MESSAGE_REQUIRES_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.ONLY_PRIVATE_MESSAGES_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestChat;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestMessage;
import com.rstepanchuk.miniplant.telegrambot.exception.MessageValidationException;
import com.rstepanchuk.miniplant.telegrambot.exception.UserNotAllowedException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.implementation.UserRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

@ExtendWith(MockitoExtension.class)
class MessageValidatorTest {

  @InjectMocks
  private MessageValidator messageValidator;
  @Mock
  private UserRepositoryImpl userRepository;


  @Test
  @DisplayName("Reject messages from group chats")
  void validateMessage_whenMessageFromGroupChat_shouldReturnInvalidInputMessage() {
    Chat chat = TelegramTestChat.builder().withType("group").build();
    Message message = TelegramTestMessage.builder().withChat(chat).build();
    MessageValidationException expectedException = assertThrows(MessageValidationException.class,
        () -> messageValidator.validateMessage(message));
    assertEquals(ONLY_PRIVATE_MESSAGES_ALLOWED, expectedException.getMessage());
  }

  @Test
  void validateMessage_whenMessageHasNoText_shouldReturnInvalidInputMessage() {
    Message message = TelegramTestMessage.getBasicMessage();
    message.setText(null);
    MessageValidationException expectedException = assertThrows(MessageValidationException.class,
        () -> messageValidator.validateMessage(message));
    assertEquals(MESSAGE_REQUIRES_TEXT, expectedException.getMessage());
  }

  @Test
  void validateMessage_whenUserIsNotFromAllowedList_shouldReturnInvalidInputMessage() {
    Message message = TelegramTestMessage.getBasicMessage();
    Long notAllowedUserId = 2L;
    message.getFrom().setId(notAllowedUserId);
    when(userRepository.findById(notAllowedUserId)).thenReturn(Optional.empty());

    UserNotAllowedException expectedException = assertThrows(UserNotAllowedException.class,
        () -> messageValidator.validateMessage(message));
    assertEquals(BOT_IS_ONLY_FOR_SPECIFIC_USERS, expectedException.getMessage());
  }

  @Test
  void validateMessage_whenUserIsFromAllowedList_shouldReturnValidUser() {
    Message message = TelegramTestMessage.getBasicMessage();
    BotUser validBotUser = new BotUser();
    when(userRepository.findById(DEFAULT_USER_ID))
        .thenReturn(Optional.of(validBotUser));

    BotUser actualUser = messageValidator.validateMessage(message);
    assertEquals(validBotUser, actualUser);
  }

}