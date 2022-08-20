package com.rstepanchuk.miniplant.telegrambot.model;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class BotUser {

  private Long id;
  private String stageId;
  private List<Integer> messagesWithMarkup = new ArrayList<>();

}
