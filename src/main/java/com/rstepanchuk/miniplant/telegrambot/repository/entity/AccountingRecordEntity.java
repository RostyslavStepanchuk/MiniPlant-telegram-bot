package com.rstepanchuk.miniplant.telegrambot.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "accounting_records")
public class AccountingRecordEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "type")
  private String type;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "account")
  private String account;

  @Column(name = "category")
  private String category;

  @Column(name = "contractor")
  private String contractor;

  @Column(name = "comment")
  private String comment;

  @OneToOne
  @JoinColumn(name = "user_id_fk")
  private BotUserEntity user;

  @Column(name = "entered")
  private LocalDate entered;
}
