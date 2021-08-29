package com.rstepanchuk.miniplant.telegrambot.bot;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNTING_INC_EXP;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.UNDEFINED;

import java.util.HashMap;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStage;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageHandler;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageIncomeOrExpense;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageMain;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageUndefined;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

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
      DialogStageMain dialogStageMain,
      DialogStageUndefined dialogStageUndefined,
      DialogStageIncomeOrExpense dialogStageIncomeOrExpense) {
    HashMap<String, DialogStage<? extends BotApiMethod>> stages = new HashMap<>();
    stages.put(MAIN, dialogStageMain);
    stages.put(UNDEFINED, dialogStageUndefined);
    stages.put(ACCOUNTING_INC_EXP, dialogStageIncomeOrExpense);
    return new DialogStageHandler(stages, userRepository);
  }


}
