package com.rstepanchuk.miniplant.telegrambot.google;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.api.client.http.HttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.google.auth.GoogleCredentialsManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class GoogleServiceFactoryTest {

  @InjectMocks
  private GoogleServiceFactory subject;

  @Mock
  private GoogleCredentialsManager credentialsManager;

  @Mock
  private HttpTransport httpTransport;

  @Test
  @DisplayName("Returns Sheets service")
  void getSheetsService() {
    Update basicUpdate = TelegramTestUpdate.getBasicUpdate();
    Sheets sheetsService = subject.getSheetsService(basicUpdate);

    assertEquals(Sheets.class, sheetsService.getClass());
  }
}