package com.rstepanchuk.miniplant.telegrambot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "users")
public class BotUser {
  @Id
  private Long id;
}
