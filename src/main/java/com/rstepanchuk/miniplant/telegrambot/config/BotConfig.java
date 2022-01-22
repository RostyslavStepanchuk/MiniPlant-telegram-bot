package com.rstepanchuk.miniplant.telegrambot.config;

import com.rstepanchuk.miniplant.telegrambot.bot.MessageValidator;
import com.rstepanchuk.miniplant.telegrambot.bot.MiniPlantBot;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageUndefined;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
public class BotConfig {

  @Bean
  TelegramLongPollingBot miniPlantBot(MessageValidator messageValidator,
                                      DialogStageHandler dialogStageHandler) {
    return new MiniPlantBot(messageValidator, dialogStageHandler);
  }

  @Bean
  MessageValidator messageValidator(UserRepository userRepository) {
    return new MessageValidator(userRepository);
  }

  @Bean
  DialogStageHandler dialogStageHandler(
      UserRepository userRepository,
      ApplicationContext context,
      DialogStageUndefined undefinedStage) {
    return new DialogStageHandler(userRepository, context, undefinedStage);
  }


}
