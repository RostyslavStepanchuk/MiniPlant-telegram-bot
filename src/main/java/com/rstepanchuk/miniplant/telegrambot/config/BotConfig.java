package com.rstepanchuk.miniplant.telegrambot.config;

import com.rstepanchuk.miniplant.telegrambot.bot.UserFilter;
import com.rstepanchuk.miniplant.telegrambot.bot.MiniPlantBot;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageUndefined;
import com.rstepanchuk.miniplant.telegrambot.repository.AccountingRecordsRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import com.rstepanchuk.miniplant.telegrambot.repository.implementation.MenuOptionsRepositoryImpl;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
public class BotConfig {

  @Bean
  TelegramLongPollingBot miniPlantBot(UserFilter userFilter,
                                      DialogStageHandler dialogStageHandler) {
    return new MiniPlantBot(userFilter, dialogStageHandler);
  }

  @Bean
  UserFilter userFilter(UserRepository userRepository) {
    return new UserFilter(userRepository);
  }

  @Bean
  DialogStageHandler dialogStageHandler(
      UserRepository userRepository,
      ApplicationContext context,
      DialogStageUndefined undefinedStage) {
    return new DialogStageHandler(userRepository, context, undefinedStage);
  }

  @Bean
  AccountingService accountingService(AccountingRecordsRepository accountingRecordsRepository) {
    return new AccountingServiceImpl(accountingRecordsRepository);
  }

  @Bean
  MenuOptionsRepository menuOptionsRepository() {
    return new MenuOptionsRepositoryImpl();
  }


}
