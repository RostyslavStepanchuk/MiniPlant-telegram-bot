package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.MESSAGE_REQUIRES_TEXT;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.ONLY_PRIVATE_MESSAGES_ALLOWED;

import com.rstepanchuk.miniplant.telegrambot.exception.MessageValidationException;
import com.rstepanchuk.miniplant.telegrambot.exception.UserNotAllowedException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
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
  public BotUser validateMessage(Message message) {

    // Reject if it's not a user message
    if (!message.isUserMessage()) {
      throw new MessageValidationException(ONLY_PRIVATE_MESSAGES_ALLOWED);
    }

    // Reject if there is no text in message
    if (!message.hasText()) {
      throw new MessageValidationException(MESSAGE_REQUIRES_TEXT);
    }

    // Reject if user is not in the list of allowed
    Long userId = message.getFrom().getId();

    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotAllowedException(BOT_IS_ONLY_FOR_SPECIFIC_USERS));
  }
}
