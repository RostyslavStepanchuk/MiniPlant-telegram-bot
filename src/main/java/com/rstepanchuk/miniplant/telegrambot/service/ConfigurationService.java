package com.rstepanchuk.miniplant.telegrambot.service;

import com.rstepanchuk.miniplant.telegrambot.model.BotUser;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;

public interface ConfigurationService {

  SheetsTableCredentials updateTableCredentialsForUser(
      BotUser user,
      SheetsTableCredentials newCredentials);

}
