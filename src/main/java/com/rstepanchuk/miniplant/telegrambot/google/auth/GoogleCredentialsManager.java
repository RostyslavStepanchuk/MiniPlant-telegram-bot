package com.rstepanchuk.miniplant.telegrambot.google.auth;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.FOLLOW_AUTH_URL;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.GOOGLE_AUTH_EXCEPTION;

import java.io.IOException;
import java.util.Optional;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Slf4j
public class GoogleCredentialsManager {

  private final GoogleAuthorizationCodeFlow authCodeFlow;
  private final TelegramLongPollingBot bot;
  private final VerificationCodeReceiver receiver;


  public Credential getCredentials(Update update) {
    Long userId = update.getMessage().getFrom().getId();

    return Optional.ofNullable(getExistingCredentials(authCodeFlow, userId))
        .orElseGet(() -> authorize(authCodeFlow, update));
  }

  private Credential getExistingCredentials(GoogleAuthorizationCodeFlow authCodeFlow,
                                            Long userId) {
    try {
      Credential credential = authCodeFlow.loadCredential(String.valueOf(userId));
      if (credential == null) {
        return null;
      }

      if (credential.getExpirationTimeMilliseconds() != null) {
        return credential;
      }

      if (credential.getRefreshToken() != null) {
        return credential.refreshToken() ? credential : null;
      }

      return null;
    } catch (Exception e) {
      log.error("Error while getting or refreshing stored credentials", e);
      return null;
    }
  }

  private Credential authorize(GoogleAuthorizationCodeFlow authCodeFlow,
                               Update update) {
    try {
      String redirectUri = receiver.getRedirectUri();
      AuthorizationCodeRequestUrl authorizationUrl =
          authCodeFlow.newAuthorizationUrl().setRedirectUri(redirectUri);

      bot.execute(MessageBuilder.basicMessage(update, FOLLOW_AUTH_URL));
      bot.execute(MessageBuilder.basicMessage(update, authorizationUrl.build()));
      String code = receiver.waitForCode();
      TokenResponse response =
          authCodeFlow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
      return authCodeFlow.createAndStoreCredential(response,
          String.valueOf(update.getMessage().getFrom().getId()));
    } catch (Exception e) {
      log.error("Unable to authorize on Google", e);
      throw new GoogleApiException(GOOGLE_AUTH_EXCEPTION);
    } finally {
      try {
        receiver.stop();
      } catch (IOException e) {
        log.error("Unable to stop the receiver", e);
      }
    }
  }

}
