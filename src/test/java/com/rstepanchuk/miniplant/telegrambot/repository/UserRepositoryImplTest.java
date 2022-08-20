package com.rstepanchuk.miniplant.telegrambot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.UserDao;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

  @InjectMocks
  UserRepositoryImpl subject;

  @Mock
  UserDao userDao;

  @Mock
  UserMapper mapper;

  @Test
  @DisplayName("save - transforms user to entity before saving")
  void save_transformsModelToEntity() {
    BotUser botUser = new BotUser();
    subject.save(botUser);
    verify(mapper).toBotUserEntity(botUser);
  }

  @Test
  @DisplayName("save - saves entity to dao")
  void save_savesEntityToDao() {
    // given
    BotUser botUser = new BotUser();
    BotUserEntity expected = new BotUserEntity();
    doReturn(expected).when(mapper).toBotUserEntity(botUser);
    // when
    subject.save(botUser);

    // then
    verify(userDao).save(expected);
  }

  @Test
  @DisplayName("findById - gets value from dao")
  void findById_searchesValueInDao() {
    long input = 1L;
    subject.findById(input);

    verify(userDao).findById(input);
  }

  @Test
  @DisplayName("findById - maps result to model")
  void findById_mapsResultToModel() {
    // given
    long input = 1L;
    BotUserEntity givenUser = new BotUserEntity();
    BotUser expected = new BotUser();
    doReturn(Optional.of(givenUser))
        .when(userDao).findById(input);
    doReturn(expected).when(mapper).toBotUser(any());
    // when
    Optional<BotUser> actual = subject.findById(input);
    // then
    assertEquals(Optional.of(expected), actual);
    verify(mapper).toBotUser(givenUser);
  }

}