package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageIncomeOrExpense;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageMain;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageUndefined;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

  @Bean
  MiniPlantBot miniPlantBot(MessageValidator messageValidator,
                            DialogStageHandler dialogStageHandler) {
    return new MiniPlantBot(messageValidator, dialogStageHandler);
  }

  @Bean
  MessageValidator messageValidator(UserRepository userRepository) {
    return new MessageValidator(userRepository);
  }

  @Bean
  DialogStageMain dialogStageMain() {
    return new DialogStageMain();
  }

  @Bean
  DialogStageUndefined dialogStageUndefined() {
    return new DialogStageUndefined();
  }

  @Bean
  DialogStageIncomeOrExpense dialogStageIncomeExpenseChoice() {
    return new DialogStageIncomeOrExpense();
  }

  @Bean
  DialogStageHandler dialogStageHandler(
      UserRepository userRepository,
      ApplicationContext context,
      DialogStageUndefined undefinedStage) {
    return new DialogStageHandler(userRepository, context, undefinedStage);
  }


}
