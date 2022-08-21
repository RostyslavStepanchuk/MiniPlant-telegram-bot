package com.rstepanchuk.miniplant.telegrambot.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class BotUser {

  private Long id;
  private String stageId;
  private List<Integer> messagesWithMarkup = new ArrayList<>();
  @Getter(AccessLevel.NONE)
  private SheetsTableCredentials sheetsCredentials;

  public Optional<SheetsTableCredentials> getSheetsTableCredentials() {
    return Optional.ofNullable(sheetsCredentials);
  }

}
