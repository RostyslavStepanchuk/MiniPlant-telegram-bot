package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestChat;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestMessage;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.MESSAGE_REQUIRES_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.ONLY_PRIVATE_MESSAGES_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageValidatorTest {

  @InjectMocks
  private MessageValidator messageValidator;
  @Mock
  private UserRepository userRepository;

  private final Long ALLOWED_USER_ID = DEFAULT_USER_ID;
  private final Long NOT_ALLOWED_USER_ID = 2L;


  @Test
  void validateMessage_whenMessageFromGroupChat_shouldReturnInvalidInputMessage() {
    Chat chat = TelegramTestChat.builder().withType("group").build();
    Message message = TelegramTestMessage.builder().withChat(chat).build();
    Optional<String> result = messageValidator.validateMessage(message);

    assertTrue(result.isPresent(), "Invalid message should be present");
    assertEquals(ONLY_PRIVATE_MESSAGES_ALLOWED, result.get());
  }

  @Test
  void validateMessage_whenMessageHasNoText_shouldReturnInvalidInputMessage() {
    Message message = TelegramTestMessage.getBasicMessage();
    message.setText(null);
    Optional<String> result = messageValidator.validateMessage(message);

    assertTrue(result.isPresent(), "Invalid message should be present");
    assertEquals(MESSAGE_REQUIRES_TEXT, result.get());
  }

  @Test
  void validateMessage_whenUserIsNotFromAllowedList_shouldReturnInvalidInputMessage() {
    Message message = TelegramTestMessage.getBasicMessage();
    message.getFrom().setId(NOT_ALLOWED_USER_ID);
    when(userRepository.findById(NOT_ALLOWED_USER_ID)).thenReturn(Optional.empty());

    Optional<String> result = messageValidator.validateMessage(message);

    assertTrue(result.isPresent(), "Invalid message should be present");
    assertEquals(BOT_IS_ONLY_FOR_SPECIFIC_USERS, result.get());
  }

  @Test
  void validateMessage_whenUserIsFromAllowedList_shouldReturnEmptyOptional() {
    Message message = TelegramTestMessage.getBasicMessage();
    when(userRepository.findById(ALLOWED_USER_ID))
        .thenReturn(Optional.of(new BotUser()));

    Optional<String> result = messageValidator.validateMessage(message);

    assertTrue(result.isEmpty(), "Invalid message should be empty");
  }

}