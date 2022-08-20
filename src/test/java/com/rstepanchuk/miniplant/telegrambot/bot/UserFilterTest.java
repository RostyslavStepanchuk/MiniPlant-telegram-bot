package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.BOT_IS_ONLY_FOR_SPECIFIC_USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.UserNotAllowedException;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.implementation.UserRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class UserFilterTest {

  @InjectMocks
  private UserFilter subject;
  @Mock
  private UserRepositoryImpl userRepository;

  @Test
  @DisplayName("authorizeUser - throws exception for invalid users")
  void authorizeUser_whenUserIsNotFromAllowedList_shouldReturnInvalidInputMessage() {
    Long notAllowedUserId = 2L;
    when(userRepository.findById(notAllowedUserId)).thenReturn(Optional.empty());

    // when & then
    UserNotAllowedException expectedException = assertThrows(UserNotAllowedException.class,
        () -> subject.authorizeUser(notAllowedUserId));
    assertEquals(BOT_IS_ONLY_FOR_SPECIFIC_USERS, expectedException.getMessage());
  }

  @Test
  @DisplayName("authorizeUser - returns user model")
  void authorizeUser_whenUserIsFromAllowedList_shouldReturnValidUser() {
    BotUser validBotUser = new BotUser();
    when(userRepository.findById(DEFAULT_USER_ID))
        .thenReturn(Optional.of(validBotUser));

    BotUser actualUser = subject.authorizeUser(DEFAULT_USER_ID);
    assertEquals(validBotUser, actualUser);
  }

  @Test
  @DisplayName("getUserId - gets user ID from message if present")
  void getUserId_shouldGetUserIdFromMessage() {
    Update givenUpdate = TelegramTestUpdate.getBasicMessageUpdate();
    Optional<Long> actual = subject.getUserId(givenUpdate);
    assertTrue(actual.isPresent());
    assertEquals(DEFAULT_USER_ID, actual.get());
  }

  @Test
  @DisplayName("getUserId - gets user ID from callback query if present")
  void getUserId_shouldGetUserIdFromCallbackIfPresent() {
    Update givenUpdate = TelegramTestUpdate.getBasicCallbackUpdate();
    Optional<Long> actual = subject.getUserId(givenUpdate);
    assertTrue(actual.isPresent());
    assertEquals(DEFAULT_USER_ID, actual.get());
  }

  @Test
  @DisplayName("getUserId - returns empty optional if neither source has id")
  void getUserId_shouldReturnEmptyOptionalIfNoMessageOrCallback() {
    Update givenUpdate = new Update();
    Optional<Long> actual = subject.getUserId(givenUpdate);
    assertTrue(actual.isEmpty());
  }

}