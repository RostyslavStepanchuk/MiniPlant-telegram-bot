package com.rstepanchuk.miniplant.telegrambot.repository.implementation;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.ACCOUNT_SELECTION;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Stages.MAIN;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class MenuOptionsServiceImplTest {

  private final MenuOptionsServiceImpl subject = new MenuOptionsServiceImpl();

  @Test
  void getOptionsForTheStage() {
    assertEquals(List.of("нал", "приват", "моно", "каса"),
        subject.getOptionsForTheStage(ACCOUNT_SELECTION));
    assertTrue(subject.getOptionsForTheStage(MAIN).isEmpty());
  }
}