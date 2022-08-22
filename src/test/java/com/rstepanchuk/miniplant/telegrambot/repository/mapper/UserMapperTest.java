package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.MarkupMessageEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.SheetsTableCredentialsEntity;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

  private static final Long ID = 1L;
  private static final String STAGE = Constants.Stages.MAIN;

  @InjectMocks
  UserMapper subject = Mappers.getMapper(UserMapper.class);

  @Mock
  private SheetsTablesCredentialsMapper sheetsCredentialsMapper;

  @Test
  @DisplayName("toBotUser - copies all properties")
  void toBotUser_copiesAllProperties() {
    // given
    BotUserEntity given = new BotUserEntity();
    given.setId(ID);
    given.setStageId(STAGE);
    // when
    BotUser actual = subject.toBotUser(given);
    // then
    assertEquals(ID, actual.getId());
    assertEquals(STAGE, actual.getStageId());
  }

  @Test
  @DisplayName("toBotUser - converts MarkupMessages to list of ID's")
  void toBotUserEntity_shouldConvertMarkupsToIds() {
    // given
    int messageId = 1;
    int secondMessageId = 2;

    MarkupMessageEntity message1 = new MarkupMessageEntity();
    message1.setId(messageId);
    MarkupMessageEntity message2 = new MarkupMessageEntity();
    message2.setId(secondMessageId);

    BotUserEntity given = new BotUserEntity();
    given.setMessagesWithMarkup(List.of(message1, message2));

    // when & then
    BotUser actual = subject.toBotUser(given);
    assertIterableEquals(
        List.of(messageId, secondMessageId),
        actual.getMessagesWithMarkup());
  }

  @Test
  @DisplayName("toBotUser - accepts null")
  void toBotUser_acceptsNull() {
    assertNull(subject.toBotUser(null));
  }

  @Test
  @DisplayName("toBotUser - copies mapped SheetsTableCredentials")
  void toBotUser_shouldCopySheetsTableCredentials() {
    BotUserEntity given = new BotUserEntity();
    SheetsTableCredentialsEntity givenCredentials = mock(SheetsTableCredentialsEntity.class);
    given.setSheetsCredentials(givenCredentials);

    SheetsTableCredentials mappedCredentials = mock(SheetsTableCredentials.class);
    doReturn(mappedCredentials).when(sheetsCredentialsMapper).toModel(any());

    BotUser actual = subject.toBotUser(given);

    assertTrue(actual.getSheetsTableCredentials().isPresent());
    assertEquals(mappedCredentials, actual.getSheetsTableCredentials().get());
    verify(sheetsCredentialsMapper).toModel(givenCredentials);
  }

  @Test
  @DisplayName("toBotUserEntity - copies all properties")
  void toBotUserEntity_copiesAllProperties() {
    // given
    BotUser given = new BotUser();
    given.setId(ID);
    given.setStageId(STAGE);
    // when
    BotUserEntity actual = subject.toBotUserEntity(given);
    // then
    assertEquals(ID, actual.getId());
    assertEquals(STAGE, actual.getStageId());
  }

  @Test
  @DisplayName("toBotUserEntity - accepts null")
  void toBotUserEntity_acceptsNull() {
    assertNull(subject.toBotUserEntity(null));
  }

  @Test
  @DisplayName("toBotUserEntity - converts ID's to MarkupMessageEntities")
  void toBotUserEntity_shouldConvertIdsToMarkups() {
    // given
    int messageId = 1;
    int secondMessageId = 2;

    BotUser given = new BotUser();
    given.setMessagesWithMarkup(List.of(messageId, secondMessageId));

    MarkupMessageEntity message1 = new MarkupMessageEntity();
    message1.setId(messageId);
    MarkupMessageEntity message2 = new MarkupMessageEntity();
    message2.setId(secondMessageId);

    // when & then
    BotUserEntity entity = subject.toBotUserEntity(given);
    MarkupMessageEntity actual1 = entity.getMessagesWithMarkup().get(0);
    MarkupMessageEntity actual2 = entity.getMessagesWithMarkup().get(1);
    assertEquals(messageId, actual1.getId());
    assertEquals(secondMessageId, actual2.getId());
  }

  @Test
  @DisplayName("toBotUserEntity - accepts nulls instead of MarkupMessagesEntities")
  void toBotUserEntity_shouldAcceptNullMarkups() {
    BotUser given = new BotUser();
    given.setMessagesWithMarkup(null);
    assertNull(subject.toBotUserEntity(given).getMessagesWithMarkup());
  }

  @Test
  @DisplayName("toBotUserEntity - copies mapped SheetsTableCredentialsEntity")
  void toBotUserEntity_shouldCopySheetsTableCredentials() {
    BotUser given = new BotUser();
    SheetsTableCredentials givenCredentials = mock(SheetsTableCredentials.class);
    given.setSheetsCredentials(givenCredentials);

    SheetsTableCredentialsEntity mappedCredentials = mock(SheetsTableCredentialsEntity.class);
    doReturn(mappedCredentials).when(sheetsCredentialsMapper).toEntity(any());

    BotUserEntity actual = subject.toBotUserEntity(given);

    assertEquals(mappedCredentials, actual.getSheetsCredentials());
    verify(sheetsCredentialsMapper).toEntity(givenCredentials);
  }
}