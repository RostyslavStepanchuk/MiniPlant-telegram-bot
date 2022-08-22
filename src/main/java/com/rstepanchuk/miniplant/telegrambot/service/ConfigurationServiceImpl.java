package com.rstepanchuk.miniplant.telegrambot.service;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import com.rstepanchuk.miniplant.telegrambot.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

  private final UserRepository userRepository;

  @Override
  public SheetsTableCredentials updateTableCredentialsForUser(
      BotUser botUser,
      SheetsTableCredentials newCredentials) {

    SheetsTableCredentials updated = botUser.getSheetsTableCredentials()
        .map(credentials -> credentials.merge(newCredentials))
        .orElse(newCredentials);

    botUser.setSheetsCredentials(updated);

    userRepository.save(botUser);
    return updated;
  }
}
