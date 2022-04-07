package com.rstepanchuk.miniplant.telegrambot.repository.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserMapperTest {

  private static final Long ID = 1L;
  private static final String STAGE = Constants.Stages.MAIN;

  UserMapper subject = Mappers.getMapper(UserMapper.class);

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
  @DisplayName("toBotUserEntity - accepts null")
  void toBotUser_acceptsNull() {
    assertNull(subject.toBotUser(null));
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
  @DisplayName("toBotUser - accepts null")
  void toBotUserEntity_acceptsNull() {
    assertNull(subject.toBotUserEntity(null));
  }

}