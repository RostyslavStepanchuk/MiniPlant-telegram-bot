package com.rstepanchuk.miniplant.telegrambot.config;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.UserDao;
import com.rstepanchuk.miniplant.telegrambot.repository.implementation.UserRepositoryImpl;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

  @Bean
  UserRepository userRepository(UserDao userDao, UserMapper userMapper) {
    return new UserRepositoryImpl(userDao, userMapper);
  }
}
