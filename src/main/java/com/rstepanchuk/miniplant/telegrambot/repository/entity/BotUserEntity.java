package com.rstepanchuk.miniplant.telegrambot.repository.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class BotUserEntity {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "stage")
  private String stageId;

}
