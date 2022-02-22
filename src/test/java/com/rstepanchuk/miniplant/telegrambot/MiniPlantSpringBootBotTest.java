package com.rstepanchuk.miniplant.telegrambot;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.starter.TelegramBotStarterConfiguration;

@SpringBootTest(
webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = TelegramBotStarterConfiguration.class)
class MiniPlantSpringBootBotTest {

  @Autowired
  TelegramLongPollingBot miniPlantBot;

  @Test
  @DisplayName("Beans are loaded")
  void contextLoads(){
    assertNotNull(miniPlantBot);
  }

}