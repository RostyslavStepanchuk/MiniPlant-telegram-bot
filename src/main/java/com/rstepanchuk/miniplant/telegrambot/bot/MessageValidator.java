package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.MESSAGE_REQUIRES_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.ONLY_PRIVATE_MESSAGES_ALLOWED;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import java.util.Optional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageValidator {

  private final UserRepository userRepository;

  public MessageValidator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Verifies if incoming message can be processed by the bot.
   * Provides response message text if input doesn't pass verification
   *
   * @param message telegram message
   * @return user response about validation been failed or empty optional if message is valid
   */
  public Optional<String> validateMessage(Message message) {

    // Reject if there is no message
    if (!message.isUserMessage()) {
      return Optional.of(ONLY_PRIVATE_MESSAGES_ALLOWED);
    }

    // Reject if there is no text in message
    if (!message.hasText()) {
      return Optional.of(MESSAGE_REQUIRES_TEXT);
    }

    // Reject if user is not in the list of allowed
    Long userId = message.getFrom().getId();
    if (userRepository.findById(userId).isEmpty()) {
      return Optional.of(BOT_IS_ONLY_FOR_SPECIFIC_USERS);
    }

    return Optional.empty();
  }
}
