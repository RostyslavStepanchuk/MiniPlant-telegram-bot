package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

  @Bean
  MiniPlantBot miniPlantBot(MessageValidator messageValidator){
    return new MiniPlantBot(messageValidator);
  }

  @Bean
  MessageValidator botUserFilter(UserRepository userRepository){
    return new MessageValidator(userRepository);
  }


}
