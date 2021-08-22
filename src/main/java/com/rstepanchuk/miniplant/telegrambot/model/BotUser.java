package com.rstepanchuk.miniplant.telegrambot.model;



import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class BotUser {
  @Id
  private Long id;
}
