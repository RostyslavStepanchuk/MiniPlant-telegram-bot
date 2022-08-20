package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.exception.UserNotAllowedException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class UserFilter {

  private final UserRepository userRepository;

  public UserFilter(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public BotUser authorizeUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotAllowedException(BOT_IS_ONLY_FOR_SPECIFIC_USERS));
  }

  public Optional<Long> getUserId(Update update) {
    Long userId = null;
    if (update.hasMessage()) {
      userId = update.getMessage().getFrom().getId();
    } else if (update.hasCallbackQuery()) {
      userId = update.getCallbackQuery().getFrom().getId();
    } else {
      log.error("Unable to identify user from update {}. Update will be ignored",
          update.getUpdateId());
    }
    return Optional.ofNullable(userId);
  }
}
