package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.ONLY_PRIVATE_MESSAGES_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageValidatorTest {

  @InjectMocks
  private MessageValidator messageValidator;
  @Mock
  private UserRepository userRepository;

  @Test
  void nullParamShouldThrowNullPointer() {
    new Update();
    assertThrows(NullPointerException.class, ()-> messageValidator.validateMessage(null));
  }

  @Test
  void dummyTest() {
    boolean test = true;
    assertTrue(test);
  }

  @Test
  void groupMessageUpdateShouldReturnUserMessage() {
    Message message = new Message();
    Chat chat = new Chat();
    chat.setId(1L);
    chat.setType("group");
    message.setChat(chat);
    Optional<SendMessage> sendMessage = messageValidator.validateMessage(message);
    assertTrue(sendMessage.isPresent());
    assertTrue(sendMessage.get().getText().equals(ONLY_PRIVATE_MESSAGES_ALLOWED));
  }
}