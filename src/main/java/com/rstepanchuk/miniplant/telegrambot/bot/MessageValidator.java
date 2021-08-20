package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.ONLY_PRIVATE_MESSAGES_ALLOWED;

public class MessageValidator {

  private final UserRepository userRepository;

  public MessageValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<SendMessage> validateMessage(Message message) {

    // Reject if there is no message or user
    if (!message.isUserMessage()) {
      return Optional.of(new SendMessage(
          String.valueOf(message.getChatId()),
          ONLY_PRIVATE_MESSAGES_ALLOWED
      ));
    }
    // Reject if user is not in the list of allowed
    Long userId = message.getFrom().getId();
    if (!userRepository.findById(userId).isPresent()) {
      return Optional.of(new SendMessage(
          String.valueOf(message.getChatId()),
          BOT_IS_ONLY_FOR_SPECIFIC_USERS
      ));
    }
    return Optional.empty();
  }
}
