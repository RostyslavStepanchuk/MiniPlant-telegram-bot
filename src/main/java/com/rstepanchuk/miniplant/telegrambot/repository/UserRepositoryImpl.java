package com.rstepanchuk.miniplant.telegrambot.repository;

import java.util.Optional;
import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.UserDao;
import com.rstepanchuk.miniplant.telegrambot.repository.entity.BotUserEntity;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserDao userDao;
  private final UserMapper userMapper;

  @Override
  public void save(BotUser user) {
    BotUserEntity botUserEntity = userMapper.toBotUserEntity(user);
    userDao.save(botUserEntity);
  }

  @Override
  public Optional<BotUser> findById(Long userId) {
    return userDao.findById(userId)
        .map(userMapper::toBotUser);
  }

}
