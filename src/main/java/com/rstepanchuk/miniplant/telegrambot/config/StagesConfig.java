package com.rstepanchuk.miniplant.telegrambot.config;

import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageGoogleAuth;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageIncomeOrExpense;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageMain;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageUndefined;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import com.rstepanchuk.miniplant.telegrambot.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StagesConfig {

  @Bean(name = Constants.Stages.MAIN)
  DialogStageMain dialogStageMain(AccountingService accountingService) {
    return new DialogStageMain(accountingService);
  }

  @Bean(name = Constants.Stages.UNDEFINED)
  DialogStageUndefined dialogStageUndefined() {
    return new DialogStageUndefined();
  }

  @Bean(name = Constants.Stages.ACCOUNTING_INC_EXP)
  DialogStageIncomeOrExpense dialogStageIncomeExpenseChoice() {
    return new DialogStageIncomeOrExpense();
  }

  @Bean(name = Constants.Stages.GOOGLE_AUTH)
  DialogStageGoogleAuth dialogStageGoogleAuth(GoogleCredentialsManager manager) {
    return new DialogStageGoogleAuth(manager);
  }
}
