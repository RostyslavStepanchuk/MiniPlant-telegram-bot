package com.rstepanchuk.miniplant.telegrambot.google.auth;

import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.GOOGLE_AUTH_EXCEPTION;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

@Slf4j
@UtilityClass
public class GoogleAuthorizationUtils {

  private static GoogleClientSecrets getSecrets(
      String clientId,
      String clientSecret,
      String secretsPath) throws IOException {
    InputStream in = GoogleCredentialsManager.class.getResourceAsStream(secretsPath);
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        GsonFactory.getDefaultInstance(),
        new InputStreamReader(in));
    clientSecrets.getDetails().setClientId(clientId);
    clientSecrets.getDetails().setClientSecret(clientSecret);
    return clientSecrets;
  }

  public static GoogleAuthorizationCodeFlow createCodeFlow(
      Environment env,
      HttpTransport httpTransport) {
    try {
      String tokensPath =
          GoogleAuthorizationUtils.class.getResource("/").getPath()
              + env.getProperty("sheets.tokens_path");
      List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
      return new GoogleAuthorizationCodeFlow.Builder(
          httpTransport,
          GsonFactory.getDefaultInstance(),
          getSecrets(
              env.getProperty("sheets.client_id"),
              env.getProperty("sheets.client_secret"),
              env.getProperty("sheets.secrets_path")
          ),
          scopes)
          .setDataStoreFactory(new FileDataStoreFactory(new File(tokensPath)))
          .setAccessType("offline")
          .build();
    } catch (IOException e) {
      log.error("Unable to create code flow", e);
      throw new GoogleApiException(GOOGLE_AUTH_EXCEPTION);
    }
  }
}
