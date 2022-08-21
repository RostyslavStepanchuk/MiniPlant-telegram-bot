package com.rstepanchuk.miniplant.telegrambot.config;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages;

import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageAccountSelection;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageAmountInput;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageCategorySelection;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageGoogleAuth;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageIncomeOrExpense;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageMain;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageSheetIdSetup;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageSheetPageSetup;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageSheetsConfig;
import com.rstepanchuk.miniplant.telegrambot.bot.stages.DialogStageUndefined;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import com.rstepanchuk.miniplant.telegrambot.repository.MenuOptionsRepository;
import com.rstepanchuk.miniplant.telegrambot.service.ConfigurationService;
import com.rstepanchuk.miniplant.telegrambot.service.accounting.AccountingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StagesConfig {

  @Bean(name = Stages.MAIN)
  DialogStageMain dialogStageMain(
      DialogStageAmountInput dialogStageAmountInput,
      DialogStageSheetsConfig dialogStageSheetsConfig) {
    return new DialogStageMain(dialogStageAmountInput, dialogStageSheetsConfig);
  }

  @Bean(name = Stages.UNDEFINED)
  DialogStageUndefined dialogStageUndefined() {
    return new DialogStageUndefined();
  }

  @Bean(name = Stages.GOOGLE_AUTH)
  DialogStageGoogleAuth dialogStageGoogleAuth(GoogleCredentialsManager manager) {
    return new DialogStageGoogleAuth(manager);
  }

  @Bean(name = Stages.AMOUNT_INPUT)
  DialogStageAmountInput dialogStageAmountInput(
      AccountingService accountingService,
      MenuOptionsRepository menuOptionsRepository) {
    return new DialogStageAmountInput(accountingService, menuOptionsRepository);
  }

  @Bean(name = Stages.TYPE_SELECTION)
  DialogStageIncomeOrExpense dialogStageIncomeExpenseChoice(
      AccountingService accountingService,
      MenuOptionsRepository menuOptionsRepository
  ) {
    return new DialogStageIncomeOrExpense(accountingService, menuOptionsRepository);
  }

  @Bean(name = Stages.ACCOUNT_SELECTION)
  DialogStageAccountSelection dialogStageAccountSelection(
      AccountingService accountingService,
      MenuOptionsRepository menuOptionsRepository) {
    return new DialogStageAccountSelection(accountingService, menuOptionsRepository);
  }

  @Bean(name = Stages.CATEGORY_SELECTION)
  DialogStageCategorySelection dialogStageCategorySelection(
      AccountingService accountingService) {
    return new DialogStageCategorySelection(accountingService);
  }

  @Bean(name = Stages.CONFIGURATIONS)
  DialogStageSheetsConfig dialogStageCategorySelection() {
    return new DialogStageSheetsConfig();
  }

  @Bean(name = Stages.SHEET_ID_SETUP)
  DialogStageSheetIdSetup dialogStageCategorySelection(
      ConfigurationService configurationService) {
    return new DialogStageSheetIdSetup(configurationService);
  }

  @Bean(name = Stages.SHEET_PAGE_SETUP)
  DialogStageSheetPageSetup dialogStageSheetPageSetup(
      ConfigurationService configurationService) {
    return new DialogStageSheetPageSetup(configurationService);
  }
}
