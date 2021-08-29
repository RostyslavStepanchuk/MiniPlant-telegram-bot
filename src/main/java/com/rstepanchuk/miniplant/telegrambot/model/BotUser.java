package com.rstepanchuk.miniplant.telegrambot.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class BotUser {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "stage")
  private String stageId;

}
