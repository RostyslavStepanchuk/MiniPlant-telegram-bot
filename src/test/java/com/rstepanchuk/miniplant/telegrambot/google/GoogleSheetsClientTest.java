package com.rstepanchuk.miniplant.telegrambot.google;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.rstepanchuk.miniplant.telegrambot.exception.GoogleApiException;
import com.rstepanchuk.miniplant.telegrambot.model.SheetsTableCredentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsClientTest {

  private static final String SHEET_ID = "1fadfu324khdf32";
  private static final String PAGE_NAME = "Accounting";
  private static final String RANGE = "A1:A1";
  private static final String FULL_RANGE = PAGE_NAME + "!" + RANGE;
  private static final String RAW_INPUT_OPTION = "RAW";


  @InjectMocks
  private GoogleSheetsClient subject;

  @Spy
  private Sheets sheets = new Sheets(new MockHttpTransport(), new MockJsonFactory(),
      new MockGoogleCredential.Builder().build());

  @Mock
  private Sheets.Spreadsheets.Values.Append append;

  @Mock
  private Sheets.Spreadsheets.Values values;

  @Mock
  private Sheets.Spreadsheets spreadsheets;

  void mockSpreadsheetsChain() throws IOException {
    doReturn(spreadsheets).when(sheets).spreadsheets();
    doReturn(values).when(spreadsheets).values();
    doReturn(append).when(values).append(any(), any(), any());
    doReturn(append).when(append).setValueInputOption(any());
    doReturn(new AppendValuesResponse()).when(append).execute();
  }

  @Test
  @DisplayName("appendRow - uses appends class")
  void appendRow_shouldBuildChainUpToAppendClass() throws IOException {
    // given
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    ArrayList<Object> values = new ArrayList<>();

    mockSpreadsheetsChain();

    // when
    subject.appendRow(credentials, values);

    // then
    verify(sheets).spreadsheets();
    verify(spreadsheets).values();
  }


  @Test
  @DisplayName("appendRow - appends sheetsId from credentials")
  void appendRow_shouldAppendSheetIdFromCredentials() throws IOException {
    // given
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    credentials.setSheetId(SHEET_ID);
    ArrayList<Object> givenValues = new ArrayList<>();

    mockSpreadsheetsChain();

    // when
    subject.appendRow(credentials, givenValues);

    // then
    verify(values).append(eq(SHEET_ID), any(), any());
  }

  @Test
  @DisplayName("appendRow - appends valid page range")
  void appendRow_shouldAppendPageRangeFromCredentials() throws IOException {
    // given
    SheetsTableCredentials credentials = mock(SheetsTableCredentials.class);
    credentials.setPageName(PAGE_NAME);
    credentials.setRange(RANGE);
    ArrayList<Object> givenValues = new ArrayList<>();

    mockSpreadsheetsChain();
    doReturn(FULL_RANGE).when(credentials).getTableFullAddress();

    // when
    subject.appendRow(credentials, givenValues);

    // then
    verify(credentials).getTableFullAddress();
    verify(values).append(any(), eq(FULL_RANGE), any());
  }

  @Test
  @DisplayName("appendRow - appends values")
  void appendRow_shouldAppendValues() throws IOException {
    // given
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    List<Object> givenValues = List.of("Test_value");
    ValueRange expectedRange = new ValueRange();
    expectedRange.setValues(List.of(givenValues));

    mockSpreadsheetsChain();

    // when
    subject.appendRow(credentials, givenValues);

    // then
    verify(values).append(any(), any(), eq(expectedRange));
  }

  @Test
  @DisplayName("appendRow - sets RAW value input option")
  void appendRow_shouldSetRawValueInputOption() throws IOException {
    // given
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    List<Object> givenValues = List.of("Test_value");

    mockSpreadsheetsChain();

    // when
    subject.appendRow(credentials, givenValues);

    // then
    verify(append).setValueInputOption(RAW_INPUT_OPTION);
  }

  @Test
  @DisplayName("appendRow - executes call to sheets")
  void appendRow_executesCallToSheets() throws IOException {
    // given
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    List<Object> givenValues = List.of("Test_value");

    mockSpreadsheetsChain();

    // when
    AppendValuesResponse actual = subject.appendRow(credentials, givenValues);

    // then
    verify(append).execute();
    assertEquals(new AppendValuesResponse(), actual);
  }

  @Test
  @DisplayName("appendRow - throws Google Api Exception")
  void appendRow_whenExceptionThrown_shouldThrowGoogleApiException() throws IOException {
    // given
    SheetsTableCredentials credentials = new SheetsTableCredentials();
    List<Object> givenValues = List.of("Test_value");
    doThrow(IOException.class).when(append).execute();

    doReturn(spreadsheets).when(sheets).spreadsheets();
    doReturn(values).when(spreadsheets).values();
    doReturn(append).when(values).append(any(), any(), any());
    doReturn(append).when(append).setValueInputOption(any());

    // when & then
    assertThrows(GoogleApiException.class,
        () -> subject.appendRow(credentials, givenValues));
  }

}