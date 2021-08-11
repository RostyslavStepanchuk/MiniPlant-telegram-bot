package com.rstepanchuk.miniplant.telegrambot.bot;

import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

  @Bean
  MiniPlantBot miniPlantBot(BotUserFilter botUserFilter){
    return new MiniPlantBot(botUserFilter);
  }

  @Bean
  BotUserFilter botUserFilter(UserRepository userRepository){
    return new BotUserFilter(userRepository);
  }


}
