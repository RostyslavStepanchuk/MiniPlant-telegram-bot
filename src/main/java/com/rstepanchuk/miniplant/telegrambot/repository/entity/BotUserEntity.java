package com.rstepanchuk.miniplant.telegrambot.repository.entity;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
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

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<MarkupMessageEntity> messagesWithMarkup;

}
