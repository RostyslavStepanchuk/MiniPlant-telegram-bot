package com.rstepanchuk.miniplant.telegrambot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

  @InjectMocks
  private ConfigurationServiceImpl subject;

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("updateTableCredentialsForUser - merges credentials if user already has one")
  void updateTableCredentialsForUser_shouldMergeCredentials() {
    // given
    SheetsTableCredentials givenCreds = mock(SheetsTableCredentials.class);
    SheetsTableCredentials currentCreds = mock(SheetsTableCredentials.class);
    BotUser user = new BotUser();
    user.setSheetsCredentials(currentCreds);

    doReturn(currentCreds).when(currentCreds).merge(any());

    // when & then
    SheetsTableCredentials actual = subject.updateTableCredentialsForUser(user, givenCreds);
    verify(currentCreds).merge(givenCreds);
    assertEquals(currentCreds, actual);
  }

  @Test
  @DisplayName("updateTableCredentialsForUser - sets incoming credentials if user didn't have")
  void updateTableCredentialsForUser_shouldSetIncomingCredentialsIfCurrentIsNull() {
    // given
    SheetsTableCredentials givenCreds = mock(SheetsTableCredentials.class);
    SheetsTableCredentials currentCreds = mock(SheetsTableCredentials.class);
    BotUser user = new BotUser();
    user.setSheetsCredentials(currentCreds);

    // when & then
    SheetsTableCredentials actual = subject.updateTableCredentialsForUser(user, givenCreds);
    assertEquals(givenCreds, actual);
  }

  @Test
  @DisplayName("updateTableCredentialsForUser - saves user to repository")
  void updateTableCredentialsForUser_shouldSaveUser() {
    // given
    SheetsTableCredentials givenCreds = mock(SheetsTableCredentials.class);
    BotUser user = mock(BotUser.class);

    // when & then
    subject.updateTableCredentialsForUser(user, givenCreds);
    verify(userRepository).save(user);
  }
}