package com.rstepanchuk.miniplant.telegrambot.google.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleAuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
class GoogleAuthorizationUtilsTest {

  private static final String CLIENT_ID = "test_client_id";
  private static final String CLIENT_SECRET = "test_client_secret";
  private static final String TOKENS_PATH = "/tokens";
  private static final String SECRETS_PATH = "/google_api.json";
  private static final String SECRETS_PATH_INVALID = "";

  @Mock
  private Environment env;

  private final HttpTransport httpTransport = new MockHttpTransport();

  @Test
  @DisplayName("Should take configs from properties")
  void shouldTakeConfigsFromProperties() {
    when(env.getProperty("sheets.client_id")).thenReturn(CLIENT_ID);
    when(env.getProperty("sheets.client_secret")).thenReturn(CLIENT_SECRET);
    when(env.getProperty("sheets.secrets_path")).thenReturn(SECRETS_PATH);
    when(env.getProperty("sheets.tokens_path")).thenReturn(TOKENS_PATH);

    GoogleAuthorizationCodeFlow codeFlow = GoogleAuthorizationUtils.createCodeFlow(env, httpTransport);

    verify(env).getProperty("sheets.client_id");
    verify(env).getProperty("sheets.client_secret");
    verify(env).getProperty("sheets.secrets_path");
    verify(env).getProperty("sheets.tokens_path");
    assertEquals(CLIENT_ID, codeFlow.getClientId());
  }

  @Test
  @DisplayName("Should provide scope for Google Sheets")
  void scopeSheets() {
    when(env.getProperty("sheets.client_id")).thenReturn(CLIENT_ID);
    when(env.getProperty("sheets.client_secret")).thenReturn(CLIENT_SECRET);
    when(env.getProperty("sheets.secrets_path")).thenReturn(SECRETS_PATH);
    when(env.getProperty("sheets.tokens_path")).thenReturn(TOKENS_PATH);

    List<String> expectedScopes =
        Collections.singletonList(SheetsScopes.SPREADSHEETS);

    GoogleAuthorizationCodeFlow codeFlow = GoogleAuthorizationUtils.createCodeFlow(env, httpTransport);

    assertIterableEquals(expectedScopes, codeFlow.getScopes());
  }

  @Test
  @DisplayName("Should throw GoogleApiException if secrets missing")
  void shouldThrowGoogleApiException() {
    when(env.getProperty("sheets.client_id")).thenReturn(CLIENT_ID);
    when(env.getProperty("sheets.client_secret")).thenReturn(CLIENT_SECRET);
    when(env.getProperty("sheets.secrets_path")).thenReturn(SECRETS_PATH_INVALID);
    when(env.getProperty("sheets.tokens_path")).thenReturn(TOKENS_PATH);

    assertThrows(GoogleAuthenticationException.class, ()-> GoogleAuthorizationUtils.createCodeFlow(env,
        httpTransport));
  }

}