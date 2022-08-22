package com.rstepanchuk.miniplant.telegrambot.config;

import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepositoryImpl;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepositoryImpl;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.AccountingRecordsDao;
import com.rstepanchuk.miniplant.telegrambot.repository.dao.UserDao;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.AccountingRecordMapper;
import com.rstepanchuk.miniplant.telegrambot.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RepositoryConfig {

  @Bean
  UserRepository userRepository(UserDao userDao, UserMapper userMapper) {
    return new UserRepositoryImpl(userDao, userMapper);
  }

  @Bean
  @Primary
  AccountingRecordsRepository accountingRecordsRepository(
      AccountingRecordsDao daoEntities,
      @Qualifier("GoogleSheetsAccounting") AccountingRecordsRepository googleSheetsRepo,
      AccountingRecordMapper mapper) {
    return new AccountingRecordsRepositoryImpl(daoEntities, googleSheetsRepo, mapper);
  }
}
