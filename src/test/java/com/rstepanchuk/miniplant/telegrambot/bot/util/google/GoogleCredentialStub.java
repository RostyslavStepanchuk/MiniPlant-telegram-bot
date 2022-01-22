package com.rstepanchuk.miniplant.telegrambot.bot.util.google;

import static com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential.REFRESH_TOKEN;

import java.io.IOException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.testing.http.MockHttpTransport;
import org.mockito.Mockito;

public class GoogleCredentialStub extends Credential {

  private static final String TOKEN_SERVER_ENCODED_URL = "https://token_server.com";
  private boolean refreshingTokenExpectedResult = true;
  private boolean refreshTokenException;

  public static GoogleCredentialStub createInstance() {
    Credential.AccessMethod accessMethod = Mockito.mock(Credential.AccessMethod.class);
    Builder builder = new Builder(accessMethod);
    builder.setJsonFactory(GsonFactory.getDefaultInstance());
    builder.setTransport(new MockHttpTransport.Builder().build());
    builder.setClientAuthentication(Mockito.mock(HttpExecuteInterceptor.class));
    builder.setTokenServerEncodedUrl(TOKEN_SERVER_ENCODED_URL);
    return Mockito.spy(new GoogleCredentialStub(builder));
  }

  private GoogleCredentialStub(Builder builder) {
    super(builder);
  }

  @Override
  public Credential setFromTokenResponse(TokenResponse tokenResponse) {
    return this;
  }

  @Override
  protected TokenResponse executeRefreshToken() throws IOException {
    if (refreshTokenException) {
      throw new IOException("Error while refreshing token");
    }
    return refreshingTokenExpectedResult ? Mockito.mock(TokenResponse.class) : null;
  }

  public void mockRefreshToken(){
    setRefreshToken(REFRESH_TOKEN);
  }

  public void mockRefreshingTokenResult(boolean expectedResult) {
    this.refreshingTokenExpectedResult = expectedResult;
  }

  public void setExceptionOnTokenRefresh() {
    this.refreshTokenException = true;
  }

  public static void main(String[] args) {
    MockGoogleCredential build = new MockGoogleCredential.Builder().build();
  }
}
