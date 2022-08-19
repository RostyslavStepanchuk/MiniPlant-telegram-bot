package com.rstepanchuk.miniplant.telegrambot.repository.implementation;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.EXPENSES;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Buttons.INCOME;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNT_SELECTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.CATEGORY_SELECTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.TYPE_SELECTION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class MenuOptionsRepositoryImplTest {

  private final MenuOptionsRepositoryImpl subject = new MenuOptionsRepositoryImpl();

  @Test
  void getOptionsForTheStage() {
    assertDoesNotThrow(() -> subject.getOptionsByMenuName(TYPE_SELECTION));
    assertDoesNotThrow(() -> subject.getOptionsByMenuName(ACCOUNT_SELECTION));
    assertDoesNotThrow(() -> subject.getOptionsByMenuName(CATEGORY_SELECTION + "-" + INCOME));
    assertDoesNotThrow(() -> subject.getOptionsByMenuName(CATEGORY_SELECTION + "-" + EXPENSES));
    assertDoesNotThrow(() -> subject.getOptionsByMenuName(""));
  }
}