package com.rstepanchuk.miniplant.telegrambot;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.rstepanchuk.miniplant.telegrambot.config.BotConfig;
import com.rstepanchuk.miniplant.telegrambot.config.GoogleConfig;
import com.rstepanchuk.miniplant.telegrambot.config.StagesConfig;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@SpringBootTest(classes = {
    BotConfig.class,
    GoogleConfig.class,
    StagesConfig.class,
    MiniPlantSpringBootBotTest.MockedBeansConfig.class
})
class MiniPlantSpringBootBotTest {

  @Autowired
  TelegramLongPollingBot telegramLongPollingBot;

  @Test
  @DisplayName("Beans are loaded")
  void contextLoads(){
    assertNotNull(telegramLongPollingBot);
  }

  @TestConfiguration
  protected static class MockedBeansConfig {

    @Bean
    UserRepository telegramLongPollingBot() {
      return Mockito.mock(UserRepository.class);
    }

  }
}