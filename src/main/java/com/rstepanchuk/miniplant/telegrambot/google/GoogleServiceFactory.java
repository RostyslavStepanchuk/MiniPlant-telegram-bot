package com.rstepanchuk.miniplant.telegrambot.google;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public class GoogleServiceFactory {

  @Value("${sheets.app_name}")
  private String applicationName;

  private final GoogleCredentialsManager googleCredentialsManager;
  private final HttpTransport httpTransport;

  public Sheets getSheetsService(Update update) {
    return new Sheets.Builder(
        httpTransport,
        GsonFactory.getDefaultInstance(),
        googleCredentialsManager.getCredentials(update))
        .setApplicationName(applicationName)
        .build();
  }

}
