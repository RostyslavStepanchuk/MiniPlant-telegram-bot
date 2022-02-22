package com.rstepanchuk.miniplant.telegrambot.config;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.GOOGLE_API_EXCEPTION;

import java.io.IOException;
import java.security.GeneralSecurityException;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import com.rstepanchuk.miniplant.telegrambot.google.GoogleServiceFactory;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleAuthorizationUtils;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
public class GoogleConfig {

  @Bean
  GoogleCredentialsManager googleAuthorizationUtil(GoogleAuthorizationCodeFlow codeFlow,
                                                   VerificationCodeReceiver receiver) {
    return new GoogleCredentialsManager(codeFlow, receiver);
  }

  @Bean
  GoogleServiceFactory googleServiceFactory(
      GoogleCredentialsManager googleCredentialsManager,
      HttpTransport httpTransport) {
    return new GoogleServiceFactory(googleCredentialsManager, httpTransport);
  }

  @Bean
  GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(Environment env,
                                                          HttpTransport transport) {
    return GoogleAuthorizationUtils.createCodeFlow(env, transport);
  }

  @Bean
  HttpTransport googleNetHttpTransport() {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (IOException | GeneralSecurityException e) {
      log.error("Error while creating Google http transport", e);
      throw new GoogleApiException(GOOGLE_API_EXCEPTION);
    }
  }

  @Bean
  VerificationCodeReceiver localServerReceiver() {
    return new LocalServerReceiver();
  }

}
