package com.rstepanchuk.miniplant.telegrambot.config;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.GOOGLE_AUTH_EXCEPTION;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleServiceFactory;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleAuthorizationUtils;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
@Slf4j
public class GoogleConfig {

  @Bean
  GoogleCredentialsManager googleAuthorizationUtil(GoogleAuthorizationCodeFlow codeFlow,
                                                   TelegramLongPollingBot bot) {
    return new GoogleCredentialsManager(codeFlow, bot);
  }

  @Bean
  GoogleServiceFactory googleServiceFactory (
      GoogleCredentialsManager googleCredentialsManager,
      NetHttpTransport httpTransport) {
    return new GoogleServiceFactory(googleCredentialsManager, httpTransport);
  }

  @Bean
  GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(Environment env,
                                                          NetHttpTransport transport) {
    return GoogleAuthorizationUtils.createCodeFlow(env, transport);
  }

  @Bean
  NetHttpTransport googleNetHttpTransport() {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (IOException | GeneralSecurityException e) {
      log.error("Error while creating Google http transport", e);
      throw new GoogleApiException(GOOGLE_AUTH_EXCEPTION);
    }
  }

}
