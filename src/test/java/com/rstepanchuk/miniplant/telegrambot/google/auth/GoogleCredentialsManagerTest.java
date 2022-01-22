package com.rstepanchuk.miniplant.telegrambot.google.auth;

import static com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUser.DEFAULT_USER_ID;
import static com.rstepanchuk.miniplant.telegrambot.util.Constants.Messages.FOLLOW_AUTH_URL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.rstepanchuk.miniplant.telegrambot.bot.MessageBuilder;
import com.rstepanchuk.miniplant.telegrambot.bot.util.google.GoogleCredentialStub;
import com.rstepanchuk.miniplant.telegrambot.bot.util.testinput.TelegramTestUpdate;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@ExtendWith(MockitoExtension.class)
class GoogleCredentialsManagerTest {

  private static final String REDIRECT_URI = "http://localhost";
  private static final String AUTHORIZATION_SERVER_URI = "http://testauthorize";
  private static final String GOOGLE_CLIENT_ID = "client1234";
  private static final String AUTH_CODE = "code1234";

  @InjectMocks
  private GoogleCredentialsManager subject;

  @Mock
  private GoogleAuthorizationCodeFlow authCodeFlow;

  @Mock
  private TelegramLongPollingBot bot;

  @Mock
  private VerificationCodeReceiver receiver;

  @Mock
  private GoogleAuthorizationCodeTokenRequest tokenRequestMock;

  @Mock
  private GoogleTokenResponse tokenResponse;

  @Test
  @DisplayName("Uses stored if valid credentials present")
  void usesStoredCredentials() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    Credential credentialMock = GoogleCredentialStub.createInstance();
    credentialMock.setExpirationTimeMilliseconds(10000L);
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(credentialMock);

    // when
    Credential credentials = subject.getCredentials(givenUpdate);

    // then
    assertEquals(credentialMock, credentials);
  }

  @Test
  @DisplayName("Authorizes when no stored credentials")
  void authorizesIfNoStoredCredentials() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    GoogleCredentialStub expected = GoogleCredentialStub.createInstance();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(null);
    mockAuthorizationProcess(expected);

    // when
    Credential actual = subject.getCredentials(givenUpdate);

    //then
    assertEquals(expected, actual);
    verify(authCodeFlow).createAndStoreCredential(any(), eq(String.valueOf(DEFAULT_USER_ID)));
  }

  @Test
  @DisplayName("Authorizes when no expiration time and no refreshToken")
  void authorizesIfNoExpirationTimeAndToken() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    GoogleCredentialStub invalidCredentials = GoogleCredentialStub.createInstance();
    GoogleCredentialStub validCredentials = GoogleCredentialStub.createInstance();

    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(invalidCredentials);
    mockAuthorizationProcess(validCredentials);

    // when
    Credential actual = subject.getCredentials(givenUpdate);

    //then
    assertEquals(validCredentials, actual);
    verify(authCodeFlow).createAndStoreCredential(any(), eq(String.valueOf(DEFAULT_USER_ID)));
  }

  @Test
  @DisplayName("Refreshes credentials if no expiration time & token present")
  void refreshesIfNoExpirationTimeButRefreshTokenPresent() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    GoogleCredentialStub credentialStub = GoogleCredentialStub.createInstance();
    credentialStub.mockRefreshToken();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(credentialStub);

    // when
    Credential credentials = subject.getCredentials(givenUpdate);

    // then
    assertEquals(credentialStub, credentials);
    verify(credentialStub).refreshToken();
    verify(authCodeFlow, times(0)).newTokenRequest(any(String.class));
    verify(authCodeFlow, times(0)).createAndStoreCredential(any(), any());
  }

  @Test
  @DisplayName("Authorizes when token refresh unsuccessful")
  void authorizesIfTokenRefreshUnsuccessful() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    GoogleCredentialStub invalidCredential = GoogleCredentialStub.createInstance();
    invalidCredential.mockRefreshToken();
    invalidCredential.mockRefreshingTokenResult(false);

    GoogleCredentialStub validCredential = GoogleCredentialStub.createInstance();

    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(invalidCredential);
    mockAuthorizationProcess(validCredential);

    // when
    Credential actual = subject.getCredentials(givenUpdate);

    // then
    assertEquals(validCredential, actual);
    verify(invalidCredential).refreshToken();
    verify(authCodeFlow).createAndStoreCredential(any(), eq(String.valueOf(DEFAULT_USER_ID)));
  }

  @Test
  @DisplayName("Authorizes when token refresh throws Exception")
  void authorizesIfTokenRefreshThrowsException() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    GoogleCredentialStub invalidCredential = GoogleCredentialStub.createInstance();
    invalidCredential.mockRefreshToken();
    invalidCredential.setExceptionOnTokenRefresh();

    GoogleCredentialStub validCredential = GoogleCredentialStub.createInstance();

    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(invalidCredential);
    mockAuthorizationProcess(validCredential);

    // when
    Credential actual = subject.getCredentials(givenUpdate);

    // then
    assertEquals(validCredential, actual);
    verify(invalidCredential).refreshToken();
    verify(authCodeFlow).createAndStoreCredential(any(), eq(String.valueOf(DEFAULT_USER_ID)));
  }

  @Test
  @DisplayName("When authorizing sends message to user with auth url")
  void sendsMessageWithAuthUrl() throws IOException, TelegramApiException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(null);
    mockAuthorizationProcess(null);
    String authUrl = getGoogleAuthorizationCodeRequestUrlStub().build();
    SendMessage expectedMessage1 = MessageBuilder.basicMessage(givenUpdate, FOLLOW_AUTH_URL);
    SendMessage expectedMessage2 = MessageBuilder.basicMessage(givenUpdate, authUrl);

    ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

    // when
    subject.getCredentials(givenUpdate);

    // then
    verify(bot, times(2)).execute(messageCaptor.capture());
    List<SendMessage> actualMessages = messageCaptor.getAllValues();
    assertEquals(expectedMessage1, actualMessages.get(0));
    assertEquals(expectedMessage2, actualMessages.get(1));
  }

  @Test
  @DisplayName("When authorizing captures code with receiver")
  void capturesCodeWithCodeReceiver() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(null);
    mockAuthorizationProcess(null);

    // when
    subject.getCredentials(givenUpdate);

    // then
    verify(receiver).getRedirectUri();
    verify(receiver).waitForCode();
    verify(authCodeFlow).newTokenRequest(AUTH_CODE);
  }

  @Test
  @DisplayName("When authorizing done receiver is stopped")
  void receiverStoppedWhenSuccess() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(null);
    mockAuthorizationProcess(null);

    // when
    subject.getCredentials(givenUpdate);

    // then
    verify(receiver).stop();
  }

  @Test
  @DisplayName("When authorizing fails receiver is still stopped")
  void receiverStoppedWhenAuthFails() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(null);
    when(receiver.getRedirectUri()).thenThrow(IOException.class);

    // when
    assertThrows(GoogleApiException.class, () -> subject.getCredentials(givenUpdate));

    // then
    verify(receiver).stop();
  }

  @Test
  @DisplayName("Receiver stopping fail doesn't crush the exectution")
  void noExceptionThrownIfReceiverFailsToStop() throws IOException {
    // given
    Update givenUpdate = TelegramTestUpdate.getBasicUpdate();
    when(authCodeFlow.loadCredential(String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(null);
    mockAuthorizationProcess(null);
    doThrow(IOException.class).when(receiver).stop();

    // when & then
    assertDoesNotThrow(() -> subject.getCredentials(givenUpdate));
  }

  private void mockAuthorizationProcess(Credential output) throws IOException {
    GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
        getGoogleAuthorizationCodeRequestUrlStub();

    when(receiver.getRedirectUri()).thenReturn(REDIRECT_URI);
    when(authCodeFlow.newAuthorizationUrl()).thenReturn(googleAuthorizationCodeRequestUrl);
    when(receiver.waitForCode()).thenReturn(AUTH_CODE);
    when(authCodeFlow.newTokenRequest(any())).thenReturn(tokenRequestMock);
    when(tokenRequestMock.setRedirectUri(REDIRECT_URI)).thenReturn(tokenRequestMock);
    when(tokenRequestMock.execute()).thenReturn(tokenResponse);
    when(authCodeFlow.createAndStoreCredential(tokenResponse, String.valueOf(DEFAULT_USER_ID)))
        .thenReturn(output);
  }

  private GoogleAuthorizationCodeRequestUrl getGoogleAuthorizationCodeRequestUrlStub() {
    return new GoogleAuthorizationCodeRequestUrl(
        AUTHORIZATION_SERVER_URI,
        GOOGLE_CLIENT_ID,
        REDIRECT_URI,
        Collections.singletonList(SheetsScopes.SPREADSHEETS));
  }

}